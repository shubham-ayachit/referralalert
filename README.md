# ReferralAlert

ReferralAlert is a Java-based application designed to monitor Notion databases for referral entries with upcoming deadlines. It sends notifications to alert users about these deadlines, ensuring timely action on referrals.

## Features

- **Notion Integration**: Seamlessly connects to Notion databases to fetch referral entries.
- **Deadline Monitoring**: Identifies referrals with deadlines approaching within a specified time frame.
- **Notification System**: Sends out alerts via email or other messaging platforms to notify users of upcoming deadlines.

## Getting Started

### Prerequisites

- Java JDK 11 or later
- Maven 3.6.0 or later
- Access to a Notion database with API integration enabled

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/referralAlert.git
   
2. Navigate to the project directory:
   cd referralAlert
   
3. Install dependencies using Maven:
   mvn install


Configuration
1. Obtain an integration token from Notion.
2. Configure the application properties (src/main/resources/application.properties) with your Notion API key and database ID.
   notion.apiKey=your_notion_api_key
   notion.databaseId=your_notion_database_id
3. (Optional) Configure the notification service settings based on your preference.

Running the Application
Execute the following command from the root directory of the project:
  mvn spring-boot:run

Usage
Once the application is running, it will automatically monitor the specified Notion database for entries with upcoming deadlines and send notifications based on the configured alert system.


This README provides a basic overview, setup, and usage instructions for the ReferralAlert project. Adjust the repository URL, configuration details, and any specific usage instructions according to your project's specifics.
