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

// Function to update the cart item count
async function updateCartItemCount() {
    try {
        const response = await fetch('/api/cart/count');
        if (response.ok) {
            const count = await response.json();
            const cartCountElement = document.getElementById('cart-count');
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

// Ensure the function runs when the page loads
document.addEventListener('DOMContentLoaded', function () {
    updateCartItemCount();  // Update cart count on page load
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
                <a href="product-detail/${result.productId}">
                    <img src="${result.thumbnail}" alt="Product Image" class="product-thumbnail">
                </a>
            </div>
            <div class="product-content">
                <h6 class="product-title">${result.productName}</h6>
                <div class="product-price-variant">
                    <span class="price current-price">${result.priceFormated}</span>
                </div>
            </div>
        </div>
    `;
        resultContainer.appendChild(resultItem);
    });
}

// Save changes button
document.getElementById('saveChangesBtn').addEventListener('click', function (event) {
    event.preventDefault(); // Prevent form submission
//    document.getElementById('confirmationPopup').style.display = 'block'; // Show confirmation popup

    const firstName = document.querySelector('input[name="firstName"]').value
    const lastName = document.querySelector('input[name="lastName"]').value
    const email = document.querySelector('input[name="email"]').value
    const phoneNumber = document.querySelector('input[name="phoneNumber"]').value
    const password = document.querySelector('input[name="password"]').value
    const newPassword = document.querySelector('input[name="newPassword"]').value
    const confirmPassword = document.querySelector('input[name="confirmPassword"]').value

    if (!firstName) {
        alert('First Name must be at least 3 characters.')
        return;
    }

    if (!lastName) {
        alert('Last Name must be at least 3 characters.')
        return;
    }

    if (!phoneNumber) {
        alert('Invalid phone number.')
        return;
    }

    if (newPassword && confirmPassword && !password) {
        alert('Please enter your old password.')
        return;
    }

    if (newPassword !== confirmPassword) {
        alert('Confirm password not match!')
        return
    }

    fetch('http://localhost:8080/my-account/post?' + new URLSearchParams({
            firstName,
            lastName,
            email,
            phoneNumber,
            password,
            newPassword
        }).toString()
    ).then(res => {
        if (res.status === 200) {
            Swal.fire({
                title: "Success!",
                text: "Profile updated.",
                icon: "success"
            }).then(() =>{
                location.reload();
            })
        } else {
            res.json()
                .then((json) => {
                    alert('HH' + json.message)
                })
        }
    })
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
    document.getElementById('uform').submit()
});

// Close popup if clicking outside the popup
window.addEventListener('click', function (event) {
    var confirmationPopup = document.getElementById('confirmationPopup');
    if (event.target === confirmationPopup) {
        confirmationPopup.style.display = 'none';
    }
});