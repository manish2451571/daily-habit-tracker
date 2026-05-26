const API = 'http://localhost:5000';

const habits = [
  { id: 'water',    label: 'Drink 8 Glasses of Water', emoji: '💧' },
  { id: 'exercise', label: 'Exercise / Walk 30 mins',  emoji: '🏃' },
  { id: 'read',     label: 'Read for 20 mins',         emoji: '📚' },
  { id: 'sleep',    label: 'Sleep by 11 PM',           emoji: '😴' },
  { id: 'meditate', label: 'Meditate 10 mins',         emoji: '🧘' },
];

// Show today's date
document.getElementById('today-date').textContent =
  new Date().toDateString();

// Render habit checkboxes
function renderHabits() {
  const list = document.getElementById('habit-list');
  list.innerHTML = habits.map(h => `
    <div class="habit-item">
      <input type="checkbox" id="${h.id}" name="${h.id}" />
      <span class="emoji">${h.emoji}</span>
      <label for="${h.id}">${h.label}</label>
    </div>
  `).join('');
}

// Render streaks
async function renderStreaks() {
  try {
    const res = await fetch(`${API}/streaks`);
    const data = await res.json();
    const container = document.getElementById('streaks');
    container.innerHTML = habits.map(h => `
      <div class="streak-item">
        <span class="habit-name">${h.emoji} ${h.label}</span>
        <span class="streak-badge">🔥 ${data[h.id] || 0} days</span>
      </div>
    `).join('');
  } catch {
    document.getElementById('streaks').innerHTML =
      '<p style="color:#aaa">Could not load streaks. Is the server running?</p>';
  }
}

// Save habits
document.getElementById('habit-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const checked = habits.map(h => h.id).filter(id =>
    document.getElementById(id).checked
  );

  try {
    const res = await fetch(`${API}/save`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ habits: checked, date: new Date().toISOString().split('T')[0] })
    });
    const data = await res.json();
    showMessage(data.message || 'Habits saved!');
    renderStreaks();
  } catch {
    showMessage('Error saving. Is the server running?', true);
  }
});

// Generate weekly report
async function generateReport() {
  try {
    const res = await fetch(`${API}/report`);
    const data = await res.json();
    const output = document.getElementById('report-output');
    output.classList.remove('hidden');
    output.textContent = data.report;
  } catch {
    alert('Could not generate report. Is the server running?');
  }
}

function showMessage(msg, isError = false) {
  const el = document.getElementById('message');
  el.textContent = msg;
  el.classList.remove('hidden');
  el.style.color = isError ? '#ff7d7d' : '#7dff7d';
  setTimeout(() => el.classList.add('hidden'), 3000);
}

renderHabits();
renderStreaks();
