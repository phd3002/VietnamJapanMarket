// Toggle password visibility
document.querySelectorAll('.toggle-password').forEach(item => {
    item.addEventListener('click', function () {
        const input = document.querySelector(this.getAttribute('toggle'));
        const icon = this.querySelector('i');

        if (input.type === 'password') {
            input.type = 'text'; // Show password
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password'; // Hide password
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    });
});

// Save changes button
document.getElementById('saveChangesBtn').addEventListener('click', function (event) {
    event.preventDefault(); // Prevent form submission
    document.getElementById('confirmationPopup').style.display = 'block'; // Show confirmation popup
});

document.getElementById('confirmYesBtn').addEventListener('click', function () {
    // Hide the confirmation popup
    document.getElementById('confirmationPopup').style.display = 'none';

    // Show the success popup
    document.getElementById('successPopup').style.display = 'block';
});

document.getElementById('confirmCancelBtn').addEventListener('click', function () {
    // Hide the confirmation popup
    document.getElementById('confirmationPopup').style.display = 'none';
});

document.getElementById('successOkBtn').addEventListener('click', function () {
    // Hide the success popup
    document.getElementById('successPopup').style.display = 'none';

    // Reload the page after successful confirmation
    location.reload();
});

// Close popup if clicking outside the popup
window.addEventListener('click', function (event) {
    var confirmationPopup = document.getElementById('confirmationPopup');
    if (event.target === confirmationPopup) {
        confirmationPopup.style.display = 'none';
    }
});

// Search functionality
async function performSearch() {
    const searchInput = document.getElementById('prod-search').value.trim();
    console.log('Search Input:', searchInput); // Log the search input

    const searchResultSection = document.getElementById('search-results');
    const resultCountElem = document.getElementById('result-count');

    if (searchInput.length === 0) {
        searchResultSection.style.display = 'none';
        return false;
    }

    try {
        const response = await fetch(`/api/search?query=${encodeURIComponent(searchInput)}`);
        console.log('API Response Status:', response.status); // Log the response status
        const searchResults = await response.json();
        console.log('Search Results:', searchResults); // Log the search results

        if (searchResults.length > 0) {
            searchResultSection.style.display = 'block';
            resultCountElem.innerText = searchResults.length;
            updateSearchResults(searchResults);
        } else {
            searchResultSection.style.display = 'none';
        }
    } catch (error) {
        console.error('Error fetching search results:', error);
        searchResultSection.style.display = 'none';
    }

    return false;
}

function updateSearchResults(results) {
    const resultContainer = document.querySelector('.psearch-results');
    resultContainer.innerHTML = ''; // Clear previous results

    results.forEach(result => {
        const resultItem = document.getElementById('result-template').cloneNode(true);
        resultItem.style.display = 'block';
        resultItem.querySelector('.product-title a').innerText = result.productName;
        resultItem.querySelector('.current-price').innerText = `$${result.price}`;
        resultItem.querySelector('.old-price').innerText = ''; // Assuming no old price in the backend response
        resultItem.querySelector('.rating-number').innerHTML = `<span>0</span> Reviews`; // Assuming no rating in the backend response

        resultContainer.appendChild(resultItem);
    });
}