from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime, timedelta
import json
import os

app = Flask(__name__)
CORS(app)

DB_PATH = os.path.join(os.path.dirname(__file__), '..', 'Database', 'habits.json')

HABITS = ['water', 'exercise', 'read', 'sleep', 'meditate']

def load_data():
    if not os.path.exists(DB_PATH):
        return {}
    with open(DB_PATH, 'r') as f:
        return json.load(f)

def save_data(data):
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)
    with open(DB_PATH, 'w') as f:
        json.dump(data, f, indent=2)

@app.route('/save', methods=['POST'])
def save_habits():
    body = request.get_json()
    date = body.get('date', datetime.today().strftime('%Y-%m-%d'))
    checked = body.get('habits', [])

    data = load_data()
    data[date] = {habit: (habit in checked) for habit in HABITS}
    save_data(data)

    return jsonify({'message': f'Habits saved for {date}!'})

@app.route('/streaks', methods=['GET'])
def get_streaks():
    data = load_data()
    streaks = {}

    for habit in HABITS:
        streak = 0
        day = datetime.today()
        while True:
            key = day.strftime('%Y-%m-%d')
            if key in data and data[key].get(habit):
                streak += 1
                day -= timedelta(days=1)
            else:
                break
        streaks[habit] = streak

    return jsonify(streaks)

@app.route('/report', methods=['GET'])
def get_report():
    data = load_data()
    today = datetime.today()
    report_lines = ['=== Weekly Habit Report ===', '']

    for i in range(6, -1, -1):
        day = today - timedelta(days=i)
        key = day.strftime('%Y-%m-%d')
        label = day.strftime('%A, %b %d')
        day_data = data.get(key, {})

        done = [h for h in HABITS if day_data.get(h)]
        total = len(HABITS)
        count = len(done)
        bar = '█' * count + '░' * (total - count)

        report_lines.append(f'{label}  [{bar}]  {count}/{total}')

    report_lines.append('')
    report_lines.append('=== Habit Completion (Last 7 Days) ===')
    report_lines.append('')

    for habit in HABITS:
        days_done = sum(
            1 for i in range(7)
            if data.get((today - timedelta(days=i)).strftime('%Y-%m-%d'), {}).get(habit)
        )
        pct = int((days_done / 7) * 100)
        bar = '█' * days_done + '░' * (7 - days_done)
        report_lines.append(f'{habit.capitalize():12} [{bar}]  {pct}%')

    return jsonify({'report': '\n'.join(report_lines)})

if __name__ == '__main__':
    app.run(debug=True, port=5000)
