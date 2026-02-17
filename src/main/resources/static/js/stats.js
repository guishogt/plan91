// Analytics & Statistics Dashboard
let practitionerId;
let charts = {};

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    practitionerId = getCurrentPractitionerId();
    if (!practitionerId) {
        alert('Please set up your account first. Practitioner ID not found.');
        return;
    }

    loadDashboard();

    // Date range selector
    document.getElementById('dateRange').addEventListener('change', () => {
        loadCharts();
    });
});

async function loadDashboard() {
    try {
        // Load all data in parallel
        await Promise.all([
            loadStatistics(),
            loadHabitAnalytics(),
            loadCharts(),
            loadHeatmap()
        ]);

        // Hide loading, show content
        document.getElementById('loadingState').classList.add('hidden');
        document.getElementById('mainContent').classList.remove('hidden');
    } catch (error) {
        console.error('Error loading dashboard:', error);
        alert('Failed to load analytics: ' + error.message);
    }
}

async function loadStatistics() {
    const response = await fetch(`/api/analytics/practitioners/${practitionerId}/statistics`);
    if (!response.ok) throw new Error('Failed to load statistics');

    const data = await response.json();

    // Update key metrics
    document.getElementById('stat-active-routines').textContent = data.routines.active;
    document.getElementById('stat-current-streak').textContent = data.streaks.currentLongest;
    document.getElementById('stat-total-completions').textContent = data.streaks.totalCompletions;
    document.getElementById('stat-consistency-score').textContent = data.consistencyScore;
}

async function loadHabitAnalytics() {
    const response = await fetch(`/api/analytics/practitioners/${practitionerId}/habits`);
    if (!response.ok) throw new Error('Failed to load habit analytics');

    const data = await response.json();

    // Populate habit performance table
    renderHabitPerformanceTable(data.habits);
}

async function loadCharts() {
    const days = parseInt(document.getElementById('dateRange').value);
    const endDate = new Date().toISOString().split('T')[0];
    const startDate = new Date(Date.now() - days * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

    // Load chart data
    const [completionTrend, habitComparison, weeklyAggregation] = await Promise.all([
        fetch(`/api/analytics/charts/completion-trend?practitionerId=${practitionerId}&startDate=${startDate}&endDate=${endDate}`).then(r => r.json()),
        fetch(`/api/analytics/charts/habit-comparison?practitionerId=${practitionerId}`).then(r => r.json()),
        fetch(`/api/analytics/charts/weekly-aggregation?practitionerId=${practitionerId}&weeks=${Math.ceil(days / 7)}`).then(r => r.json())
    ]);

    // Render charts
    renderCompletionTrendChart(completionTrend);
    renderHabitComparisonChart(habitComparison);
    renderWeeklyAggregationChart(weeklyAggregation);
}

async function loadHeatmap() {
    const response = await fetch(`/api/analytics/heatmap?practitionerId=${practitionerId}`);
    if (!response.ok) throw new Error('Failed to load heatmap');

    const data = await response.json();
    renderHeatmap(data);
}

function renderCompletionTrendChart(data) {
    const ctx = document.getElementById('completionTrendChart');

    // Destroy existing chart if it exists
    if (charts.completionTrend) {
        charts.completionTrend.destroy();
    }

    charts.completionTrend = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Completions',
                data: data.data,
                borderColor: 'rgb(37, 99, 235)',
                backgroundColor: 'rgba(37, 99, 235, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

function renderHabitComparisonChart(data) {
    const ctx = document.getElementById('habitComparisonChart');

    if (charts.habitComparison) {
        charts.habitComparison.destroy();
    }

    charts.habitComparison = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Total Completions',
                data: data.data,
                backgroundColor: 'rgba(147, 51, 234, 0.7)',
                borderColor: 'rgb(147, 51, 234)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function renderWeeklyAggregationChart(data) {
    const ctx = document.getElementById('weeklyAggregationChart');

    if (charts.weeklyAggregation) {
        charts.weeklyAggregation.destroy();
    }

    charts.weeklyAggregation = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Weekly Completions',
                data: data.data,
                backgroundColor: 'rgba(34, 197, 94, 0.7)',
                borderColor: 'rgb(34, 197, 94)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function renderHabitPerformanceTable(habits) {
    const container = document.getElementById('habitPerformanceTable');

    if (habits.length === 0) {
        container.innerHTML = '<p class="text-gray-600 text-center py-4">No habits to display</p>';
        return;
    }

    const table = `
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
                <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Habit</th>
                    <th class="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">Completions</th>
                    <th class="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">Streak</th>
                    <th class="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">Rate</th>
                </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
                ${habits.map(habit => `
                    <tr>
                        <td class="px-4 py-2 text-sm text-gray-900">${habit.habitName}</td>
                        <td class="px-4 py-2 text-sm text-center text-gray-600">${habit.totalCompletions}</td>
                        <td class="px-4 py-2 text-sm text-center text-gray-600">${habit.currentStreak}</td>
                        <td class="px-4 py-2 text-sm text-center">
                            <span class="px-2 py-1 rounded ${getCompletionRateColor(habit.avgCompletionRate)}">
                                ${habit.avgCompletionRate.toFixed(1)}%
                            </span>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.innerHTML = table;
}

function getCompletionRateColor(rate) {
    if (rate >= 90) return 'bg-green-100 text-green-800';
    if (rate >= 70) return 'bg-yellow-100 text-yellow-800';
    return 'bg-red-100 text-red-800';
}

function renderHeatmap(data) {
    const container = document.getElementById('heatmapContainer');

    if (data.days.length === 0) {
        container.innerHTML = '<p class="text-gray-600 text-center py-4">No activity data to display</p>';
        return;
    }

    // Group days by week
    const weeks = [];
    let currentWeek = [];

    // Start from the first Monday
    const firstDay = data.days[0];
    let dayOfWeek = firstDay.dayOfWeek;

    // Add empty cells for days before the start
    for (let i = 1; i < dayOfWeek; i++) {
        currentWeek.push(null);
    }

    data.days.forEach(day => {
        currentWeek.push(day);

        if (currentWeek.length === 7) {
            weeks.push(currentWeek);
            currentWeek = [];
        }
    });

    // Add remaining days
    if (currentWeek.length > 0) {
        // Pad with empty cells
        while (currentWeek.length < 7) {
            currentWeek.push(null);
        }
        weeks.push(currentWeek);
    }

    const heatmapHTML = `
        <div class="flex flex-col">
            <div class="flex mb-2 text-xs text-gray-600">
                <div class="w-8"></div>
                <div class="flex-1 flex justify-between">
                    <span>Mon</span>
                    <span>Wed</span>
                    <span>Fri</span>
                    <span>Sun</span>
                </div>
            </div>
            ${weeks.map(week => `
                <div class="flex mb-1">
                    ${week.map(day => {
                        if (!day) {
                            return '<div class="w-3 h-3 mr-1 bg-gray-100 rounded"></div>';
                        }
                        const color = getHeatmapColor(day.intensity);
                        return `<div class="w-3 h-3 mr-1 rounded cursor-pointer hover:ring-2 hover:ring-blue-500 ${color}"
                                     title="${day.date}: ${day.count} completions"></div>`;
                    }).join('')}
                </div>
            `).join('')}
            <div class="flex items-center mt-4 text-xs text-gray-600">
                <span class="mr-2">Less</span>
                <div class="w-3 h-3 bg-gray-200 rounded mr-1"></div>
                <div class="w-3 h-3 bg-green-200 rounded mr-1"></div>
                <div class="w-3 h-3 bg-green-400 rounded mr-1"></div>
                <div class="w-3 h-3 bg-green-600 rounded mr-1"></div>
                <div class="w-3 h-3 bg-green-800 rounded mr-2"></div>
                <span>More</span>
            </div>
        </div>
    `;

    container.innerHTML = heatmapHTML;
}

function getHeatmapColor(intensity) {
    switch (intensity) {
        case 0: return 'bg-gray-200';
        case 1: return 'bg-green-200';
        case 2: return 'bg-green-400';
        case 3: return 'bg-green-600';
        case 4: return 'bg-green-800';
        default: return 'bg-gray-200';
    }
}
