# Daily Habit Tracker

## How to Run

### 1. Install Python dependencies
```
pip install flask flask-cors
```

### 2. Start the Flask backend
```
cd Backend
python server.py
```

### 3. Open the frontend
Open `Frontend/index.html` in your browser.

### 4. Run Java report (optional)
Download `json-simple` jar, then:
```
cd Java
javac -cp json-simple-1.1.1.jar ReportGenerator.java
java -cp .;json-simple-1.1.1.jar ReportGenerator
```
Report saves to `Database/WeeklyReport.txt`
