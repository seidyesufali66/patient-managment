package com.pm.stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;

import java.util.List;
import java.util.stream.Collectors;

public class LocalStack extends Stack {

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        // 1️⃣ Create VPC
        Vpc vpc = Vpc.Builder.create(this, "PatientManagementVPC")
                .maxAzs(2)
                .vpcName("PatientManagementVPC")
                .build();

        // 2️⃣ Security Group (allow traffic to microservice ports)
        SecurityGroup sg = SecurityGroup.Builder.create(this, "ServiceSecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(22), "SSH access");
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(4000), "Patient Service");
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(4001), "Billing Service");
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(4002), "Analytics Service");
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(4004), "API Gateway");
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(4005), "Auth Service");

        // 3️⃣ IAM Role for EC2
        Role ec2Role = Role.Builder.create(this, "EC2Role")
                .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .managedPolicies(List.of(
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonSSMManagedInstanceCore"),
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonS3ReadOnlyAccess")
                ))
                .build();

        // ✅ Define IKeyPair once and reuse it for all instances
        IKeyPair ec2KeyPair = KeyPair.fromKeyPairName(this, "SeidKeyPair", "seid-ec2-key");

        // 4️⃣ Create Databases
        DatabaseInstance authServiceDb = createDatabase(vpc, "AuthServiceDB", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabase(vpc, "PatientServiceDB", "patient-service-db");

        // 5️⃣ Kafka / MSK Cluster
        createMskCluster(vpc);

        // 6️⃣ Create EC2 instances for each microservice (Dockerized)
        createMicroserviceInstance(vpc, sg, ec2Role, ec2KeyPair,
                "AuthServiceInstance", "seidyesufali/auth-service:1.0", 4005);

        createMicroserviceInstance(vpc, sg, ec2Role, ec2KeyPair,
                "BillingServiceInstance", "seidyesufali/billing-service:1.0", 4001);

        createMicroserviceInstance(vpc, sg, ec2Role, ec2KeyPair,
                "AnalyticsServiceInstance", "seidyesufali/analytics-service:1.0", 4002);

        createMicroserviceInstance(vpc, sg, ec2Role, ec2KeyPair,
                "PatientServiceInstance", "seidyesufali/patient-service:1.0", 4000);

        createMicroserviceInstance(vpc, sg, ec2Role, ec2KeyPair,
                "ApiGatewayInstance", "seidyesufali/api-gateway:1.0", 4004);
    }

    // --- MAIN ENTRY POINT ---
    public static void main(final String[] args) {
        App app = new App();
        // ✅ No BootstraplessSynthesizer (so assets work properly)
        StackProps props = StackProps.builder().build();
        new LocalStack(app, "LocalStack", props);
        app.synth();
        System.out.println("✅ CDK synthesis complete.");
    }

    // --- Helper: Create PostgreSQL Database ---
    private DatabaseInstance createDatabase(Vpc vpc, String id, String dbName) {
        return DatabaseInstance.Builder.create(this, id)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_16)
                        .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("pm_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .multiAz(false)
                .publiclyAccessible(true)
                .build();
    }

    // --- Helper: Create Kafka/MSK Cluster ---
    private void createMskCluster(Vpc vpc) {
        CfnCluster.Builder.create(this, "KafkaCluster")
                .clusterName("patient-management-kafka")
                .kafkaVersion("2.11.1")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.large")
                        .clientSubnets(vpc.getPrivateSubnets()
                                .stream()
                                .map(ISubnet::getSubnetId)
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    // --- Helper: Create EC2 Instance for Each Microservice ---
    private void createMicroserviceInstance(Vpc vpc, SecurityGroup sg, Role ec2Role,
                                            IKeyPair ec2KeyPair,
                                            String instanceId, String dockerImage, int port) {

        UserData userData = UserData.forLinux();
        userData.addCommands(
                "yum update -y",
                "yum install -y docker",
                "service docker start",
                "usermod -a -G docker ec2-user",
                String.format("docker pull %s", dockerImage),
                String.format("docker run -d -p %d:%d %s", port, port, dockerImage)
        );

        Instance.Builder.create(this, instanceId)
                .vpc(vpc)
                .securityGroup(sg)
                .role(ec2Role)
                .machineImage(MachineImage.latestAmazonLinux2())
                .instanceType(InstanceType.of(InstanceClass.T3, InstanceSize.MICRO))
                .userData(userData)
                .keyPair(ec2KeyPair)
                .build();
    }
}
