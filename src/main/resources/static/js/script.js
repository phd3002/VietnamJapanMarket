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
function performSearch() {
    const searchInput = document.getElementById('prod-search').value.trim();

    // Get the search result section and result template
    const searchResultSection = document.getElementById('search-results');
    const resultCountElem = document.getElementById('result-count');

    if (searchInput.length === 0) {
        // Hide the results section if no search input is provided
        searchResultSection.style.display = 'none';
        return false;
    }

    // Simulate getting search results from the backend
    let searchResults = simulateSearchResults(searchInput); // This should be replaced with actual backend call

    if (searchResults.length > 0) {
        // Show the search results section and update the count
        searchResultSection.style.display = 'block';
        resultCountElem.innerText = searchResults.length;

        // Dynamically update the product listing with results
        updateSearchResults(searchResults);
    } else {
        // Hide if no results are found
        searchResultSection.style.display = 'none';
    }

    return false; // Prevent actual form submission
}

function simulateSearchResults(query) {
    // Simulate search results; replace with actual backend integration
    return [
        { title: "Product 1", price: "$29.99", oldPrice: "$49.99", rating: "100+" },
        { title: "Product 2", price: "$19.99", oldPrice: "$39.99", rating: "50+" }
    ];
}

function updateSearchResults(results) {
    const resultContainer = document.querySelector('.psearch-results');
    resultContainer.innerHTML = ''; // Clear previous results

    results.forEach(result => {
        const resultItem = document.getElementById('result-template').cloneNode(true);
        resultItem.style.display = 'block';
        resultItem.querySelector('.product-title a').innerText = result.title;
        resultItem.querySelector('.current-price').innerText = result.price;
        resultItem.querySelector('.old-price').innerText = result.oldPrice;
        resultItem.querySelector('.rating-number').innerHTML = `<span>${result.rating}</span> Reviews`;

        resultContainer.appendChild(resultItem);
    });
}