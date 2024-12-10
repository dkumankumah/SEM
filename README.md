# School Event Management

## Project Description
The School Event Management App is a comprehensive platform designed to enhance communication and event organization within the school community. This application caters to various stakeholders including students, teachers, staff, administrators, and limited access users such as parents and bus drivers.
Key features of the application include:
* Dashboard: Displays a Upcoming Events list where users can add or remove events. Students can only add events from an All Events list onto their own calendar.
* Event Management: Teachers can manage the Upcoming Events list, adding, editing, and deleting events as needed.
* Grade-Specific Events: Students view events specific to their grade, while staff and teachers see all school events.
* Subscription based events: Students can subscribe to specific event announcements based on clubs or sports they participate in.
* Notifications: Integrated notifications system that alerts users about upcoming events, with options for scheduling reminders.
* User Authentication: Includes a Sign Up/Sign In page with email verification to authenticate users as students or teachers.
* Event Details: Clicking on an event displays full details including date, description, location, and a Google Maps link to the event location.

The app aims to streamline school communications, improve event attendance tracking, and foster greater engagement within the school community.

## How to Install and Run the Project
### Prerequisites
* Android Studio (latest version)
* Kotlin plugin for Android Studio
* Android SDK (minimum API level 23)
* Firebase account for backend services

### Installation Steps
1. Clone the repository:
```bash
git clone https://github.com/dkumankumah/SEM.git
```
2. Open the project in Android Studio:
   * Launch Android Studio
   * Select "Open an Existing Project"
   * Navigate to the cloned repository and select the project folder
3. Set up Firebase:
   * Create a new Firebase project in the Firebase Console
   * Add an Android app to your Firebase project and follow the setup instructions
   * Download the google-services.json file and place it in the app module of your project
4. Sync the project with Gradle files:
   * Android Studio should automatically prompt you to sync the project
   * If not, go to File > Sync Project with Gradle Files
5. Build the project:
```bash
   ./gradlew build
```
6. Run the app:
   * Connect an Android device or use an emulator
   * Click the "Run" button in Android Studio