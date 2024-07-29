document.addEventListener('DOMContentLoaded', function() {
    const settingsForm = document.getElementById('settings-form');

    if (settingsForm) {
        settingsForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const preferredCategory = settingsForm.preferredCategory.value;
            updateSettings(preferredCategory);
        });
    }
});

function updateSettings(preferredCategory) {
    fetch('/settings', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({ preferredCategory })
    })
    .then(response => {
        if (response.ok) {
            alert('Settings updated successfully.');
        } else {
            alert('Failed to update settings. Please try again.');
        }
    })
    .catch(error => console.error('Error updating settings:', error));
}
