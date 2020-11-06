## Checkout package locally

#### 1. Checkout code and generate the gradle wrapper
```bash
brazil workspace create --name AWSCloudFormationResourceProviderEC2NI
cd AWSCloudFormationResourceProviderEC2NI
brazil workspace --vs AWSCloudFormationResourceProviderEC2NI/development
brazil workspace --p AWSCloudFormationResourceProvidersNetworkInsights
cd src/AWSCloudFormationResourceProvidersNetworkInsights
brazil workspace use --platform AL2012
brazil-build wrapper
```

#### 2. Open in IntelliJ
* 'File' -> 'New' -> 'Project from Existing Sources' 
* Select the package root
* Import as gradle project
* 'Brazil' -> 'Sync from Workspace'