# Weather API Demo

A simple Android application that demonstrates fetching and displaying weather data from the weather API. This project is built to showcase integration with RESTful APIs and clean implementation using modern Android development best practices.

## Features
- Search location & fetch current weather data from the Weather API
- Display weather details such as temperature, humidity, and conditions
- Simple and clean UI using Material Design principles
- Network communication handled using Retrofit

---

## Requirements
- **Android Studio**: Latest version (recommended)
- **Minimum SDK**: 28
- **Target SDK**: 35
- **Kotlin**: 2.0

---

## Setup Instructions
Follow these steps to set up and run the project:

### 1. Clone the Repository
```bash
git clone https://github.com/raylee4204/weather-api-demo.git
cd weather-api-demo
```

### 2. Open the Project in Android Studio
- Open **Android Studio**.
- Select **Open an Existing Project**.
- Navigate to the project folder and open it.

### 3. Configure the Weather API Key
This project uses the Weather API. You need to obtain an API key:
1. Sign up for a free account at [Weather API](https://weatherapi.com/).
2. Generate your API key.
3. Add the API key to the project:
   - Open `local.properties` file in the root directory (or create it if it doesn't exist).
   - Add the following line:
     ```
     WEATHER_API_KEY=your_api_key_here
     ```

### 4. Build and Run the Project
- Click the **Build** option in the top menu.
- Connect an Android device (or use an emulator).
- Click **Run** or use the shortcut `Shift + F10`.

---

## Libraries Used
- **Retrofit**: For making HTTP requests
- **Moshi**: JSON parsing
- **ViewModel & LiveData**: Lifecycle-aware UI components
- **Coil**: Image loading library
- **Material Design**: For UI components

---

## Project Structure
```
weather-api-demo/
|-- app/
    |-- src/
        |-- main/
            |-- java/
                |-- com.weatherapi.demo/
                    |-- ui/         # UI components and Activities
                    |-- api/    # API service and Retrofit configuration
                    |-- di/      # Hilt modules for dependency injection
                    |-- repository/ # Repository to fetch data from API
            |-- res/
                |-- layout/         # XML layout files
                |-- values/         # Colors, Strings, Dimensions
|-- build.gradle
|-- settings.gradle
|-- local.properties (add API key here)
```

---

## Contact
For questions or suggestions, feel free to reach out:
- **Email**: me@kanghee.dev
☁️
