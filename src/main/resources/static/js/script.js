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

    if (searchInput.length === 0) {
        return false;  // Exit if there's no input
    }

    try {
        // Send the search request to the backend
        const response = await fetch(`/api/search?query=${encodeURIComponent(searchInput)}`);

        if (response.ok) {
            const searchResults = await response.json();
            console.log('Search results:', searchResults);  // Log the search results
            updateSearchResults(searchResults);  // Function to update the UI
        } else {
            console.error('Search request failed:', response.status);
        }
    } catch (error) {
        console.error('Error fetching search results:', error);
    }

    return false;  // Prevent form submission and page reload
}

function updateSearchResults(results) {
    const resultContainer = document.querySelector('.search-results');
    resultContainer.innerHTML = '';  // Clear previous results

    if (results.length === 0) {
        document.getElementById('result-count').textContent = '0';
        document.getElementById('search-results').style.display = 'none';
        return;
    }

    document.getElementById('result-count').textContent = results.length;
    document.getElementById('search-results').style.display = 'block';

    results.forEach(result => {
        const resultItem = document.createElement('div');
        resultItem.classList.add('result-item');
        resultItem.innerHTML = `
        <div class="axil-product-list">
            <div class="thumbnail">
                <a href="#">
                    <img src="${result.thumbnail}" alt="Product Image" class="product-thumbnail">
                </a>
            </div>
            <div class="product-content">
                <h6 class="product-title">${result.productName}</h6>
                <div class="product-price-variant">
                    <span class="price current-price">$${result.price}</span>
                </div>
            </div>
        </div>
    `;
        resultContainer.appendChild(resultItem);
    });
}

// cap nhat so luong san pham trong gio hang tren header
async function updateCartItemCount() {
    try {
        const response = await fetch('/api/cart/count');
        if (response.ok) {
            const count = await response.json();
            console.log("Fetched count:", count);  // Log the fetched count
            const cartCountElement = document.getElementById('cart-count');
            console.log("Cart count element:", cartCountElement);  // Log the element to ensure it exists
            if (cartCountElement) {
                cartCountElement.textContent = count; // Update the cart count on the page
            }
        } else {
            console.error('Failed to fetch cart item count:', response.status);
        }
    } catch (error) {
        console.error('Error while updating cart item count:', error);
    }
}

// dam bao DOM da duoc load truoc khi chay script
document.addEventListener('DOMContentLoaded', function () {
    console.log("DOM content is loaded, script is running...");
    updateCartItemCount();  // Update cart count on page load
});