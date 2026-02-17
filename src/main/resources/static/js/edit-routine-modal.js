// Edit Routine Modal - Edit routine settings and archive routines
console.log('edit-routine-modal.js loaded');

let editRoutineId = null;

function openEditRoutineModal(routineId, habitName, recurrenceType, startDate, targetDays) {
    console.log('openEditRoutineModal called', routineId);
    editRoutineId = routineId;

    // Create modal if it doesn't exist
    let modal = document.getElementById('editRoutineModal');
    if (!modal) {
        createEditRoutineModal();
        modal = document.getElementById('editRoutineModal');
    }

    // Pre-fill form with current values
    document.getElementById('editHabitName').textContent = habitName;
    document.getElementById('editStartDate').value = startDate;
    document.getElementById('editRecurrenceType').value = recurrenceType;
    document.getElementById('editTargetDays').value = targetDays;

    // Clear any previous errors
    const errorDiv = document.getElementById('editRoutineError');
    if (errorDiv) errorDiv.classList.add('hidden');

    modal.classList.remove('hidden');
}

function closeEditRoutineModal() {
    const modal = document.getElementById('editRoutineModal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

function createEditRoutineModal() {
    const modalHtml = `
        <div id="editRoutineModal" class="hidden fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" onclick="if(event.target === this) closeEditRoutineModal()">
            <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full mx-4">
                <div class="p-6">
                    <!-- Header -->
                    <div class="flex items-center justify-between mb-6">
                        <h2 class="text-2xl font-bold text-gray-900">Edit Routine</h2>
                        <button onclick="closeEditRoutineModal()" class="text-gray-400 hover:text-gray-600">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                            </svg>
                        </button>
                    </div>

                    <!-- Habit Name (read-only) -->
                    <p class="text-gray-600 mb-6">Editing: <strong id="editHabitName"></strong></p>

                    <!-- Form -->
                    <form id="editRoutineForm" onsubmit="handleEditRoutineSubmit(event)">
                        <!-- Start Date -->
                        <div class="mb-4">
                            <label for="editStartDate" class="block text-sm font-medium text-gray-700 mb-2">Start Date</label>
                            <input type="date" id="editStartDate" name="startDate"
                                   class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent">
                        </div>

                        <!-- Frequency -->
                        <div class="mb-4">
                            <label for="editRecurrenceType" class="block text-sm font-medium text-gray-700 mb-2">Frequency</label>
                            <select id="editRecurrenceType" name="recurrenceType"
                                    class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent">
                                <option value="DAILY">Every day</option>
                                <option value="WEEKDAYS">Weekdays (Mon-Fri)</option>
                                <option value="WEEKENDS">Weekends (Sat-Sun)</option>
                                <option value="TIMES_PER_WEEK_3">3 times per week</option>
                                <option value="TIMES_PER_WEEK_4">4 times per week</option>
                                <option value="TIMES_PER_WEEK_5">5 times per week</option>
                            </select>
                        </div>

                        <!-- Target Days -->
                        <div class="mb-6">
                            <label for="editTargetDays" class="block text-sm font-medium text-gray-700 mb-2">Target Days</label>
                            <input type="number" id="editTargetDays" name="targetDays" min="1" max="365"
                                   class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent">
                        </div>

                        <!-- Error Message -->
                        <div id="editRoutineError" class="hidden mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                            <p class="text-red-800 text-sm"></p>
                        </div>

                        <!-- Save Button -->
                        <button type="submit"
                                class="w-full py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-all mb-4">
                            Save Changes
                        </button>
                    </form>

                    <!-- Archive Section -->
                    <div class="border-t border-gray-200 pt-4 mt-2">
                        <button onclick="handleArchiveRoutine()"
                                class="w-full py-3 bg-red-50 text-red-600 font-semibold rounded-lg hover:bg-red-100 transition-all border border-red-200">
                            Archive Routine
                        </button>
                        <p class="text-xs text-gray-500 mt-2 text-center">Archiving hides the routine from your dashboard. You can view archived routines later.</p>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);
}

async function handleEditRoutineSubmit(event) {
    event.preventDefault();

    const startDate = document.getElementById('editStartDate').value;
    const recurrenceType = document.getElementById('editRecurrenceType').value;
    const targetDays = parseInt(document.getElementById('editTargetDays').value);

    const data = {
        startDate: startDate || null,
        recurrenceType: recurrenceType,
        targetDays: targetDays
    };

    try {
        const response = await secureFetch(`/api/routines/${editRoutineId}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });

        if (response.ok) {
            closeEditRoutineModal();
            // Reload dashboard to reflect changes
            await loadDashboardData();
        } else {
            const errorData = await response.json();
            showEditError(errorData.message || 'Failed to update routine');
        }
    } catch (error) {
        showEditError('Network error: ' + error.message);
    }
}

async function handleArchiveRoutine() {
    if (!confirm('Are you sure you want to archive this routine? It will be hidden from your dashboard.')) {
        return;
    }

    try {
        const response = await secureFetch(`/api/routines/${editRoutineId}/archive`, {
            method: 'POST'
        });

        if (response.ok) {
            closeEditRoutineModal();
            // Reload dashboard to reflect changes
            await loadDashboardData();
        } else {
            const errorData = await response.json();
            showEditError(errorData.message || 'Failed to archive routine');
        }
    } catch (error) {
        showEditError('Network error: ' + error.message);
    }
}

function showEditError(message) {
    const errorDiv = document.getElementById('editRoutineError');
    if (errorDiv) {
        errorDiv.querySelector('p').textContent = message;
        errorDiv.classList.remove('hidden');
    }
}
