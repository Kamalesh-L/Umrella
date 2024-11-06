# UmRella App

Welcome to the **UmRella App** repository! This app offers a convenient and innovative way to access umbrella stations across campus, with integrated weather forecasting. Built as part of the Mobile Application Development course at **Amrita School of Computing**, UmRella enhances campus life by providing real-time umbrella availability, booking options, and weather forecasts for a seamless experience.

## ☂️ Project Overview
The **UmRella App** aims to simplify umbrella access for users by letting them locate, reserve, and return umbrellas at designated campus stations. The app combines weather predictions to suggest umbrella usage, making it ideal for unexpected rain showers and promoting shared resource usage among students.

## 🌦️ Weather Forecast and Umbrella Booking
This app not only enables umbrella booking but also provides a real-time weather forecast, helping users decide if an umbrella is necessary based on current conditions.

### 🌍 Data Sources
- **Weather Data**: Leveraging the OpenWeatherMap API for live weather data and forecast updates. we used a historical weather dataset from Kaggle: [Weather Dataset - Rattle Package](https://www.kaggle.com/datasets/jsphyg/weather-dataset-rattle-package).
- **Umbrella Status Tracking**: Uses Firebase Realtime Database to manage and update umbrella availability.

## 🎥 Demo Video
[UmRella App Demo](https://drive.google.com/file/d/1HPzXjYuz1V7wqBlhol_tO7tue2bEKZj-/view)

## 🌟 Key Features
- **User Authentication**: Secure Sign-Up and Login for personalized access.
- **Real-Time Weather Forecast**:
  - Displays temperature, rain predictions, and weekly weather forecasts.
  - Allows users to search for weather in any city for broader usability.
- **Umbrella Booking and Management**:
  - View available umbrella stations on a map.
  - Book an umbrella by scanning a QR code at the station.
  - Drop-off feature to safely return umbrellas.
- **Alerts and Notifications**:
  - Prompts users when umbrellas are in use and reminds them of return deadlines.
  - Notification if weather conditions indicate rain, encouraging umbrella usage.

## 🧑‍💻 Technologies and Tools
- **Programming Language**: Kotlin
- **Development Environment**: Android Studio
- **APIs and Frameworks**:
  - **OpenWeatherMap API** for weather data.
  - **Firebase Realtime Database** for umbrella tracking and status updates.
  - **TensorFlow Lite** (optional) for ML-based weather predictions and umbrella suggestions.

### 🚀 Machine Learning Model
- **Model Type**: SVC model converted to TensorFlow Lite for real-time weather predictions.
- **Attributes Used**: Temperature, humidity, wind speed, pressure, dew point, and cloud cover.

## 🛠️ Getting Started
### Prerequisites
- Android Studio installed on your machine.
- A physical Android device (recommended) or an emulator with QR code scanning capability.

### Installation
1. **Clone this repository**:
   ```bash
   git clone https://github.com/yourusername/UmRellaApp.git
2. **Open in Android Studio** and sync Gradle.

3. **Set Up Firebase**:
   - Configure Firebase Authentication and Realtime Database in the project.

4. **Add OpenWeatherMap API Key**:
   - Sign up at [OpenWeatherMap](https://openweathermap.org/) and obtain an API key.
   - Insert your API key in the `WeatherService` file.

### Usage
1. **Login or Register** to access all features.
2. **View and Book Umbrellas**: Locate a nearby station and scan the QR code to reserve an umbrella.
3. **Check Weather Forecast**: Use the weather tab to check for upcoming rain and prepare accordingly.

## 🚀 Future Improvements
- **Enhanced Predictive Model**: Improve the accuracy of weather-based umbrella recommendations.
- **Notifications for Extreme Weather**: Alerts for heavy rain, enabling proactive umbrella bookings.
- **Station Locator**: Advanced mapping features to find the nearest station.
