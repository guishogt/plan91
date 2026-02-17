/**
 * Calendar component for displaying routine completion history.
 *
 * Features:
 * - Monthly grid view with day cells
 * - Color coding: green (completed), red (missed), blue outline (scheduled)
 * - Month navigation (previous/next)
 * - Tooltip showing entry details
 */

class RoutineCalendar {
    constructor(containerId, routineId) {
        this.container = document.getElementById(containerId);
        this.routineId = routineId;
        this.currentYearMonth = this.getCurrentYearMonth();
        this.calendarData = null;

        this.render();
        this.loadCalendarData();
    }

    getCurrentYearMonth() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        return `${year}-${month}`;
    }

    async loadCalendarData() {
        try {
            const response = await fetch(`/api/routines/${this.routineId}/calendar?yearMonth=${this.currentYearMonth}`);
            if (!response.ok) {
                throw new Error('Failed to load calendar data');
            }
            this.calendarData = await response.json();
            this.renderCalendar();
        } catch (error) {
            console.error('Error loading calendar data:', error);
            this.showError('Failed to load calendar data');
        }
    }

    render() {
        this.container.innerHTML = `
            <div class="bg-white rounded-lg shadow-md p-4">
                <!-- Calendar Header -->
                <div class="flex items-center justify-between mb-4">
                    <button id="prev-month" class="p-2 hover:bg-gray-100 rounded">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
                        </svg>
                    </button>
                    <h3 id="calendar-title" class="text-lg font-semibold"></h3>
                    <button id="next-month" class="p-2 hover:bg-gray-100 rounded">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                        </svg>
                    </button>
                </div>

                <!-- Day Labels -->
                <div class="grid grid-cols-7 gap-1 mb-2">
                    <div class="text-center text-xs font-medium text-gray-600">Sun</div>
                    <div class="text-center text-xs font-medium text-gray-600">Mon</div>
                    <div class="text-center text-xs font-medium text-gray-600">Tue</div>
                    <div class="text-center text-xs font-medium text-gray-600">Wed</div>
                    <div class="text-center text-xs font-medium text-gray-600">Thu</div>
                    <div class="text-center text-xs font-medium text-gray-600">Fri</div>
                    <div class="text-center text-xs font-medium text-gray-600">Sat</div>
                </div>

                <!-- Calendar Grid -->
                <div id="calendar-grid" class="grid grid-cols-7 gap-1">
                    <!-- Days will be inserted here -->
                </div>

                <!-- Legend -->
                <div class="mt-4 flex flex-wrap gap-4 text-xs">
                    <div class="flex items-center gap-1">
                        <div class="w-4 h-4 bg-green-500 rounded"></div>
                        <span>Completed</span>
                    </div>
                    <div class="flex items-center gap-1">
                        <div class="w-4 h-4 border-2 border-blue-500 rounded"></div>
                        <span>Scheduled</span>
                    </div>
                    <div class="flex items-center gap-1">
                        <div class="w-4 h-4 bg-gray-200 rounded"></div>
                        <span>Not scheduled</span>
                    </div>
                </div>

                <!-- Stats -->
                <div id="calendar-stats" class="mt-4 grid grid-cols-3 gap-4 text-center border-t pt-4">
                    <!-- Stats will be inserted here -->
                </div>
            </div>
        `;

        // Add event listeners
        document.getElementById('prev-month').addEventListener('click', () => this.navigateMonth(-1));
        document.getElementById('next-month').addEventListener('click', () => this.navigateMonth(1));
    }

    renderCalendar() {
        if (!this.calendarData) return;

        // Update title
        const [year, month] = this.currentYearMonth.split('-');
        const date = new Date(year, month - 1, 1);
        const monthName = date.toLocaleString('default', { month: 'long', year: 'numeric' });
        document.getElementById('calendar-title').textContent = monthName;

        // Create entry lookup map
        const entryMap = new Map();
        this.calendarData.entries.forEach(entry => {
            entryMap.set(entry.date, entry);
        });

        // Create scheduled days set
        const scheduledSet = new Set(this.calendarData.scheduledDays);

        // Calculate calendar grid
        const firstDay = new Date(year, month - 1, 1);
        const lastDay = new Date(year, month, 0);
        const startingDayOfWeek = firstDay.getDay(); // 0 = Sunday
        const daysInMonth = lastDay.getDate();

        // Render calendar grid
        const grid = document.getElementById('calendar-grid');
        grid.innerHTML = '';

        // Add empty cells for days before the first of the month
        for (let i = 0; i < startingDayOfWeek; i++) {
            const emptyCell = document.createElement('div');
            emptyCell.className = 'aspect-square';
            grid.appendChild(emptyCell);
        }

        // Add cells for each day of the month
        const today = new Date().toISOString().split('T')[0];
        for (let day = 1; day <= daysInMonth; day++) {
            const dateStr = `${year}-${month}-${String(day).padStart(2, '0')}`;
            const entry = entryMap.get(dateStr);
            const isScheduled = scheduledSet.has(dateStr);
            const isToday = dateStr === today;

            const cell = this.createDayCell(day, dateStr, entry, isScheduled, isToday);
            grid.appendChild(cell);
        }

        // Render stats
        this.renderStats();
    }

    createDayCell(day, dateStr, entry, isScheduled, isToday) {
        const cell = document.createElement('div');
        cell.className = 'aspect-square flex items-center justify-center text-sm rounded cursor-pointer relative';

        // Apply styling based on status
        if (entry && entry.completed) {
            cell.className += ' bg-green-500 text-white font-semibold hover:bg-green-600';
        } else if (isScheduled) {
            cell.className += ' border-2 border-blue-500 hover:bg-blue-50';
        } else {
            cell.className += ' bg-gray-100 text-gray-400';
        }

        // Today border
        if (isToday) {
            cell.className += ' ring-2 ring-purple-500';
        }

        cell.textContent = day;

        // Add tooltip if there's an entry
        if (entry) {
            cell.title = this.getEntryTooltip(entry);
            cell.addEventListener('click', () => this.showEntryDetails(entry));
        }

        return cell;
    }

    getEntryTooltip(entry) {
        let tooltip = `Date: ${entry.date}\nStatus: ${entry.completed ? 'Completed' : 'Incomplete'}`;
        if (entry.value !== null) {
            tooltip += `\nValue: ${entry.value}`;
        }
        if (entry.notes) {
            tooltip += `\nNotes: ${entry.notes}`;
        }
        return tooltip;
    }

    showEntryDetails(entry) {
        // Could open a modal with detailed entry information
        // For now, just log to console
        console.log('Entry details:', entry);
    }

    renderStats() {
        const stats = this.calendarData.stats;
        const statsContainer = document.getElementById('calendar-stats');
        statsContainer.innerHTML = `
            <div>
                <div class="text-2xl font-bold text-blue-600">${stats.totalDays}</div>
                <div class="text-xs text-gray-600">Days in Month</div>
            </div>
            <div>
                <div class="text-2xl font-bold text-green-600">${stats.completedDays}</div>
                <div class="text-xs text-gray-600">Completed</div>
            </div>
            <div>
                <div class="text-2xl font-bold text-purple-600">${stats.completionRate.toFixed(1)}%</div>
                <div class="text-xs text-gray-600">Completion Rate</div>
            </div>
        `;
    }

    navigateMonth(delta) {
        const [year, month] = this.currentYearMonth.split('-').map(Number);
        const date = new Date(year, month - 1 + delta, 1);
        const newYear = date.getFullYear();
        const newMonth = String(date.getMonth() + 1).padStart(2, '0');
        this.currentYearMonth = `${newYear}-${newMonth}`;
        this.loadCalendarData();
    }

    showError(message) {
        this.container.innerHTML = `
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                ${message}
            </div>
        `;
    }
}

// Make it globally available
window.RoutineCalendar = RoutineCalendar;
