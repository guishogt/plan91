/**
 * Progress visualization components for routine tracking.
 *
 * Features:
 * - Progress ring (circular progress indicator)
 * - Streak badges
 * - Statistics cards
 */

class RoutineProgress {
    constructor(containerId, routineId) {
        this.container = document.getElementById(containerId);
        this.routineId = routineId;
        this.analytics = null;

        this.loadAnalytics();
    }

    async loadAnalytics() {
        try {
            const response = await fetch(`/api/routines/${this.routineId}/analytics`);
            if (!response.ok) {
                throw new Error('Failed to load analytics');
            }
            this.analytics = await response.json();
            this.render();
        } catch (error) {
            console.error('Error loading analytics:', error);
            this.showError('Failed to load progress data');
        }
    }

    render() {
        if (!this.analytics) return;

        this.container.innerHTML = `
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <!-- Progress Ring -->
                <div class="bg-white rounded-lg shadow-md p-6">
                    <h3 class="text-lg font-semibold mb-4 text-center">Overall Progress</h3>
                    <div class="flex justify-center mb-4">
                        ${this.renderProgressRing()}
                    </div>
                    <div class="text-center">
                        <div class="text-3xl font-bold text-blue-600">
                            ${this.analytics.progress.daysCompleted}/${this.analytics.progress.totalDays}
                        </div>
                        <div class="text-sm text-gray-600">Days Completed</div>
                        <div class="mt-2 text-sm text-gray-500">
                            ${this.analytics.progress.daysRemaining} days remaining
                        </div>
                    </div>
                </div>

                <!-- Streak Information -->
                <div class="bg-white rounded-lg shadow-md p-6">
                    <h3 class="text-lg font-semibold mb-4">Streaks</h3>
                    <div class="space-y-4">
                        <div class="flex items-center justify-between">
                            <div class="flex items-center gap-2">
                                <span class="text-2xl">🔥</span>
                                <span class="text-sm text-gray-600">Current Streak</span>
                            </div>
                            <span class="text-2xl font-bold text-orange-600">
                                ${this.analytics.streaks.current}
                            </span>
                        </div>
                        <div class="flex items-center justify-between">
                            <div class="flex items-center gap-2">
                                <span class="text-2xl">⭐</span>
                                <span class="text-sm text-gray-600">Longest Streak</span>
                            </div>
                            <span class="text-2xl font-bold text-yellow-600">
                                ${this.analytics.streaks.longest}
                            </span>
                        </div>
                        <div class="flex items-center justify-between">
                            <div class="flex items-center gap-2">
                                <span class="text-2xl">✅</span>
                                <span class="text-sm text-gray-600">Total Completions</span>
                            </div>
                            <span class="text-2xl font-bold text-green-600">
                                ${this.analytics.streaks.totalCompletions}
                            </span>
                        </div>
                        <div class="flex items-center justify-between pt-2 border-t">
                            <span class="text-sm text-gray-600">Strike Status</span>
                            <span class="text-sm font-medium ${this.analytics.streaks.hasUsedStrike ? 'text-red-600' : 'text-green-600'}">
                                ${this.analytics.streaks.hasUsedStrike ? 'Used' : 'Available'}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Statistics -->
                <div class="bg-white rounded-lg shadow-md p-6">
                    <h3 class="text-lg font-semibold mb-4">Statistics</h3>
                    <div class="space-y-4">
                        <div>
                            <div class="flex justify-between items-center mb-1">
                                <span class="text-sm text-gray-600">Completion Rate</span>
                                <span class="text-lg font-bold text-blue-600">
                                    ${this.analytics.completionRate.toFixed(1)}%
                                </span>
                            </div>
                            <div class="w-full bg-gray-200 rounded-full h-2">
                                <div class="bg-blue-600 h-2 rounded-full" style="width: ${this.analytics.completionRate}%"></div>
                            </div>
                        </div>
                        <div class="flex justify-between items-center">
                            <span class="text-sm text-gray-600">Weekly Average</span>
                            <span class="text-lg font-bold text-purple-600">
                                ${this.analytics.weeklyAverage.toFixed(1)}
                            </span>
                        </div>
                        <div class="flex justify-between items-center">
                            <span class="text-sm text-gray-600">Consistency Score</span>
                            <span class="text-lg font-bold ${this.getConsistencyColor(this.analytics.consistencyScore)}">
                                ${this.analytics.consistencyScore}
                            </span>
                        </div>
                        <div class="flex justify-between items-center">
                            <span class="text-sm text-gray-600">Pace</span>
                            <span class="text-sm font-medium ${this.getPaceColor(this.analytics.pace)}">
                                ${this.formatPace(this.analytics.pace)}
                            </span>
                        </div>
                        <div class="flex justify-between items-center pt-2 border-t">
                            <span class="text-sm text-gray-600">Days Until Completion</span>
                            <span class="text-lg font-bold text-gray-800">
                                ${this.analytics.daysUntilCompletion}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Insights -->
            <div class="mt-6 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg shadow-md p-6">
                <h3 class="text-lg font-semibold mb-3 flex items-center gap-2">
                    <span>💡</span>
                    <span>Insights</span>
                </h3>
                <div class="space-y-2 text-sm">
                    ${this.generateInsights()}
                </div>
            </div>
        `;
    }

    renderProgressRing() {
        const percentage = this.analytics.progress.percentage;
        const circumference = 2 * Math.PI * 45; // radius = 45
        const offset = circumference - (percentage / 100) * circumference;

        return `
            <svg class="transform -rotate-90" width="120" height="120">
                <!-- Background circle -->
                <circle
                    cx="60"
                    cy="60"
                    r="45"
                    stroke="#e5e7eb"
                    stroke-width="10"
                    fill="none"
                />
                <!-- Progress circle -->
                <circle
                    cx="60"
                    cy="60"
                    r="45"
                    stroke="#3b82f6"
                    stroke-width="10"
                    fill="none"
                    stroke-dasharray="${circumference}"
                    stroke-dashoffset="${offset}"
                    stroke-linecap="round"
                    class="transition-all duration-1000"
                />
                <!-- Percentage text -->
                <text
                    x="60"
                    y="60"
                    class="transform rotate-90"
                    text-anchor="middle"
                    dominant-baseline="middle"
                    font-size="20"
                    font-weight="bold"
                    fill="#3b82f6"
                    transform="rotate(90 60 60)"
                >
                    ${percentage.toFixed(0)}%
                </text>
            </svg>
        `;
    }

    getConsistencyColor(score) {
        if (score.startsWith('A')) return 'text-green-600';
        if (score.startsWith('B')) return 'text-blue-600';
        if (score.startsWith('C')) return 'text-yellow-600';
        return 'text-red-600';
    }

    getPaceColor(pace) {
        if (pace === 'on_track') return 'text-green-600';
        if (pace === 'slightly_behind') return 'text-yellow-600';
        return 'text-red-600';
    }

    formatPace(pace) {
        return pace.split('_').map(word =>
            word.charAt(0).toUpperCase() + word.slice(1)
        ).join(' ');
    }

    generateInsights() {
        const insights = [];

        // Current streak insight
        if (this.analytics.streaks.current > 0) {
            if (this.analytics.streaks.current >= 7) {
                insights.push(`🔥 Amazing! You're on a ${this.analytics.streaks.current}-day streak!`);
            } else {
                insights.push(`🔥 Keep it up! ${this.analytics.streaks.current} days in a row!`);
            }
        }

        // Progress insight
        const remaining = this.analytics.progress.daysRemaining;
        if (remaining <= 10) {
            insights.push(`🎯 You're in the home stretch! Just ${remaining} days to go!`);
        } else if (this.analytics.progress.percentage >= 50) {
            insights.push(`🎉 You've passed the halfway mark! ${remaining} days remaining.`);
        } else {
            insights.push(`💪 You've completed ${this.analytics.progress.daysCompleted} days. Keep going!`);
        }

        // Consistency insight
        if (this.analytics.consistencyScore.startsWith('A')) {
            insights.push(`⭐ Excellent consistency! Your ${this.analytics.consistencyScore} score shows great dedication.`);
        } else if (this.analytics.completionRate >= 80) {
            insights.push(`👍 Good consistency! You're completing ${this.analytics.completionRate.toFixed(0)}% of scheduled days.`);
        }

        // Pace insight
        if (this.analytics.pace === 'on_track') {
            insights.push(`✅ You're on track to complete your routine on schedule!`);
        } else if (this.analytics.pace === 'slightly_behind') {
            insights.push(`⚠️ You're slightly behind pace. Try to be more consistent!`);
        } else {
            insights.push(`🔴 You've fallen behind. Don't give up - every day counts!`);
        }

        // Strike insight
        if (!this.analytics.streaks.hasUsedStrike) {
            insights.push(`🛡️ You still have your one-time strike available!`);
        }

        return insights.map(insight => `<p>${insight}</p>`).join('');
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
window.RoutineProgress = RoutineProgress;
