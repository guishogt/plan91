// New Routine Modal - Unified flow for searching/creating habits and starting routines
console.log('new-routine-modal.js loaded');

let selectedHabitId = null;
let modalPractitionerId = null;

function openNewRoutineModal() {
    console.log('openNewRoutineModal called');
    modalPractitionerId = getCurrentPractitionerId();
    if (!modalPractitionerId) {
        alert('Please set up your account first. Practitioner ID not found.');
        return;
    }

    // Create modal if it doesn't exist
    let modal = document.getElementById('newRoutineModal');
    if (!modal) {
        createNewRoutineModal();
        modal = document.getElementById('newRoutineModal');
    }

    // Reset to step 1
    showStep(1);
    selectedHabitId = null;
    document.getElementById('habitSearchInput').value = '';
    document.getElementById('habitSearchResults').innerHTML = '';

    modal.classList.remove('hidden');
}

function closeNewRoutineModal() {
    const modal = document.getElementById('newRoutineModal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

function showStep(step) {
    document.getElementById('step1').classList.toggle('hidden', step !== 1);
    document.getElementById('step2').classList.toggle('hidden', step !== 2);
    document.getElementById('step3').classList.toggle('hidden', step !== 3);
}

function createNewRoutineModal() {
    const modalHtml = `
        <div id="newRoutineModal" class="hidden fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div class="bg-white rounded-2xl shadow-2xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
                <!-- Step 1: Search or Create Habit -->
                <div id="step1" class="p-8">
                    <div class="flex items-center justify-between mb-6">
                        <h2 class="text-3xl font-bold text-gray-900">Start New Routine</h2>
                        <button onclick="closeNewRoutineModal()" class="text-gray-400 hover:text-gray-600">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                            </svg>
                        </button>
                    </div>

                    <p class="text-gray-600 mb-6">Create a new habit or search for an existing one</p>

                    <!-- Create New Habit Button -->
                    <button onclick="showCreateHabitForm()" class="w-full py-4 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-all flex items-center justify-center mb-6">
                        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                        </svg>
                        Create New Habit
                    </button>

                    <!-- OR Divider -->
                    <div class="flex items-center my-6">
                        <div class="flex-1 border-t border-gray-300"></div>
                        <span class="px-4 text-gray-500 font-medium">OR</span>
                        <div class="flex-1 border-t border-gray-300"></div>
                    </div>

                    <!-- Search Habits -->
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">Search Existing Habits</label>
                        <input type="text" id="habitSearchInput"
                               class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                               placeholder="Type to search habits..."
                               onkeyup="searchHabits()">
                    </div>

                    <!-- Search Results -->
                    <div id="habitSearchResults" class="mb-4 max-h-60 overflow-y-auto">
                        <!-- Results will be populated here -->
                    </div>
                </div>

                <!-- Step 2: Create New Habit -->
                <div id="step2" class="hidden p-8">
                    <div class="flex items-center justify-between mb-6">
                        <button onclick="showStep(1)" class="text-gray-600 hover:text-gray-900 flex items-center">
                            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                            </svg>
                            Back
                        </button>
                        <h2 class="text-2xl font-bold text-gray-900">Create New Habit</h2>
                        <button onclick="closeNewRoutineModal()" class="text-gray-400 hover:text-gray-600">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                            </svg>
                        </button>
                    </div>

                    <form id="createHabitForm" class="space-y-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">Habit Name *</label>
                            <input type="text" id="habitName" required
                                   class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                                   placeholder="e.g., Morning Meditation">
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
                            <textarea id="habitDescription" rows="3"
                                      class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                                      placeholder="What is this habit about?"></textarea>
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">Tracking Type</label>
                            <div class="flex gap-4 mb-2">
                                <label class="flex items-center">
                                    <input type="radio" name="trackingType" value="BOOLEAN" checked class="mr-2" onchange="toggleUnitField()">
                                    <span>Checkbox (done/not done)</span>
                                </label>
                                <label class="flex items-center">
                                    <input type="radio" name="trackingType" value="NUMERIC" class="mr-2" onchange="toggleUnitField()">
                                    <span>Numeric (enter a value)</span>
                                </label>
                            </div>
                        </div>

                        <div id="unitField" class="hidden">
                            <label class="block text-sm font-medium text-gray-700 mb-2">Unit (optional)</label>
                            <input type="text" id="habitUnit"
                                   class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                                   placeholder="e.g., yards, minutes, pages, reps">
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">Visibility</label>
                            <div class="flex gap-4">
                                <label class="flex items-center">
                                    <input type="radio" name="visibility" value="PRIVATE" checked class="mr-2">
                                    <span>Private (Only me)</span>
                                </label>
                                <label class="flex items-center">
                                    <input type="radio" name="visibility" value="PUBLIC" class="mr-2">
                                    <span>Public (Share with community)</span>
                                </label>
                            </div>
                        </div>

                        <div id="habitCreateError" class="hidden p-4 bg-red-50 border border-red-200 rounded-md">
                            <p class="text-red-800"></p>
                        </div>

                        <button type="submit" class="w-full py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-all">
                            Create Habit & Continue
                        </button>
                    </form>
                </div>

                <!-- Step 3: Configure Routine -->
                <div id="step3" class="hidden p-8">
                    <div class="flex items-center justify-between mb-6">
                        <button onclick="showStep(1)" class="text-gray-600 hover:text-gray-900 flex items-center">
                            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                            </svg>
                            Back
                        </button>
                        <h2 class="text-2xl font-bold text-gray-900">Configure Routine</h2>
                        <button onclick="closeNewRoutineModal()" class="text-gray-400 hover:text-gray-600">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                            </svg>
                        </button>
                    </div>

                    <p class="text-gray-600 mb-6">Setting up routine for: <strong id="selectedHabitName"></strong></p>

                    <form id="startRoutineForm" class="space-y-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">Start Date *</label>
                            <input type="date" id="routineStartDate" required
                                   class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary-500 focus:border-transparent">
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">Frequency *</label>
                            <select id="routineRecurrence" required
                                    class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary-500 focus:border-transparent">
                                <optgroup label="Fixed Schedule">
                                    <option value="DAILY">Every Day</option>
                                    <option value="WEEKDAYS">Weekdays (Mon-Fri)</option>
                                    <option value="WEEKENDS">Weekends (Sat-Sun)</option>
                                </optgroup>
                                <optgroup label="Flexible (any days)">
                                    <option value="TIMES_PER_WEEK_1">1× per week</option>
                                    <option value="TIMES_PER_WEEK_3">3× per week</option>
                                    <option value="TIMES_PER_WEEK_4">4× per week</option>
                                    <option value="TIMES_PER_WEEK_5">5× per week</option>
                                    <option value="TIMES_PER_WEEK_6">6× per week</option>
                                </optgroup>
                            </select>
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">Duration (days) *</label>
                            <input type="number" id="routineTargetDays" required min="1" max="365" value="91"
                                   class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary-500 focus:border-transparent">
                            <p class="text-xs text-gray-500 mt-1">How many completions to finish this routine (default: 91)</p>
                        </div>

                        <div id="routineCreateError" class="hidden p-4 bg-red-50 border border-red-200 rounded-md">
                            <p class="text-red-800"></p>
                        </div>

                        <button type="submit" class="w-full py-3 bg-green-600 text-white font-semibold rounded-lg hover:bg-green-700 transition-all">
                            Start 91-Day Routine
                        </button>
                    </form>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    // Add form handlers
    document.getElementById('createHabitForm').addEventListener('submit', handleCreateHabit);
    document.getElementById('startRoutineForm').addEventListener('submit', handleStartRoutine);

    // Set default start date to today
    document.getElementById('routineStartDate').valueAsDate = new Date();
}

async function searchHabits() {
    const query = document.getElementById('habitSearchInput').value.trim();
    const resultsContainer = document.getElementById('habitSearchResults');

    if (query.length < 2) {
        resultsContainer.innerHTML = '';
        return;
    }

    try {
        const response = await secureFetch(`/api/habits/search?q=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error('Search failed');

        const habits = await response.json();

        if (habits.length === 0) {
            resultsContainer.innerHTML = '<p class="text-gray-500 text-center py-4">No habits found. Try creating a new one!</p>';
            return;
        }

        resultsContainer.innerHTML = habits.map(habit => `
            <div class="border border-gray-200 rounded-lg p-4 mb-2 hover:bg-gray-50 cursor-pointer transition-all"
                 onclick="selectHabit('${habit.id}', '${escapeHtml(habit.name)}')">
                <h4 class="font-semibold text-gray-900">${habit.name}</h4>
                ${habit.description ? `<p class="text-sm text-gray-600 mt-1">${habit.description}</p>` : ''}
                <span class="text-xs text-gray-500 mt-2 inline-block">${habit.isPublic ? '🌍 Public' : '🔒 Private'}</span>
            </div>
        `).join('');
    } catch (error) {
        console.error('Search error:', error);
        resultsContainer.innerHTML = '<p class="text-red-600 text-center py-4">Error searching habits</p>';
    }
}

function selectHabit(habitId, habitName) {
    selectedHabitId = habitId;
    document.getElementById('selectedHabitName').textContent = habitName;
    showStep(3);
}

function showCreateHabitForm() {
    showStep(2);
    document.getElementById('createHabitForm').reset();
    document.getElementById('habitCreateError').classList.add('hidden');
    document.getElementById('unitField').classList.add('hidden');
}

function toggleUnitField() {
    const trackingType = document.querySelector('input[name="trackingType"]:checked').value;
    const unitField = document.getElementById('unitField');
    if (trackingType === 'NUMERIC') {
        unitField.classList.remove('hidden');
    } else {
        unitField.classList.add('hidden');
    }
}

async function handleCreateHabit(e) {
    e.preventDefault();

    const visibility = document.querySelector('input[name="visibility"]:checked').value;
    const trackingType = document.querySelector('input[name="trackingType"]:checked').value;

    const habitData = {
        practitionerId: modalPractitionerId,
        name: document.getElementById('habitName').value,
        description: document.getElementById('habitDescription').value || null,
        trackingType: trackingType,
        numericUnit: trackingType === 'NUMERIC' ? (document.getElementById('habitUnit').value || null) : null,
        isPublic: visibility === 'PUBLIC',
        isPrivate: visibility === 'PRIVATE'
    };

    try {
        const response = await secureFetch('/api/habits', {
            method: 'POST',
            body: JSON.stringify(habitData)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to create habit');
        }

        const habit = await response.json();
        selectHabit(habit.id, habit.name);
    } catch (error) {
        const errorDiv = document.getElementById('habitCreateError');
        errorDiv.querySelector('p').textContent = error.message;
        errorDiv.classList.remove('hidden');
    }
}

async function handleStartRoutine(e) {
    e.preventDefault();

    const routineData = {
        practitionerId: modalPractitionerId,
        habitId: selectedHabitId,
        startDate: document.getElementById('routineStartDate').value,
        recurrenceType: document.getElementById('routineRecurrence').value,
        targetDays: parseInt(document.getElementById('routineTargetDays').value) || 91
    };

    try {
        const response = await secureFetch('/api/routines', {
            method: 'POST',
            body: JSON.stringify(routineData)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to start routine');
        }

        // Success! Close modal and reload dashboard
        closeNewRoutineModal();
        if (typeof loadDashboardData === 'function') {
            await loadDashboardData();
        } else {
            window.location.reload();
        }
    } catch (error) {
        const errorDiv = document.getElementById('routineCreateError');
        errorDiv.querySelector('p').textContent = error.message;
        errorDiv.classList.remove('hidden');
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
