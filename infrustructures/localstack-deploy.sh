#!/bin/bash
set -e

# Full path to your awslocal installation
AWLOCAL="C:/Users/seids/AppData/Local/Programs/Python/Python315/Scripts/awslocal.bat"

"$AWLOCAL" cloudformation deploy \
  --stack-name patient-management \
  --template-file "./cdk.out/LocalStack.template.json"

echo "✅ Deployment complete."

echo "Fetching stack events for troubleshooting..."
"$AWLOCAL" cloudformation describe-stack-events --stack-name patient-management
