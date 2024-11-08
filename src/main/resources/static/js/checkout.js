// Add event listeners to each button
document.querySelectorAll('.shipping-btn').forEach(function (button) {
    button.addEventListener('click', function (event) {
        event.preventDefault(); // Prevent default action (page jump)

        // Remove 'selected' class from all buttons
        document.querySelectorAll('.shipping-btn').forEach(function (btn) {
            btn.classList.remove('selected');
        });

        // Add 'selected' class to the clicked button
        this.classList.add('selected');

        // Hide all shipping prices
        document.querySelectorAll('.shipping-price').forEach(function (price) {
            price.style.display = 'none';
        });

        // Show the price for the selected shipping method
        document.getElementById('shipping-price-' + this.id).style.display = 'inline';
    });
});

// Show the default selected shipping method's price on page load
document.addEventListener('DOMContentLoaded', function () {
    const selectedShipping = document.querySelector('.shipping-btn.selected');
    document.getElementById('shipping-price-' + selectedShipping.id).style.display = 'inline';
});

// Add event listeners to each payment button
document.querySelectorAll('.payment-btn').forEach(function (button) {
    button.addEventListener('click', function (event) {
        event.preventDefault(); // Prevent default action (page jump)

        // Remove 'selected' class from all buttons
        document.querySelectorAll('.payment-btn').forEach(function (btn) {
            btn.classList.remove('selected');
        });

        // Add 'selected' class to the clicked button
        this.classList.add('selected');

        // Hide all payment details
        document.querySelectorAll('.payment-details').forEach(function (details) {
            details.style.display = 'none';
        });

        // Show the details for the selected payment method
        document.getElementById(this.id + 'Details').style.display = 'block';
    });
});

// Update the total shipping fee and total order price when the shipping method is changed
document.getElementById("shipping-method").addEventListener("change", function () {
    const shippingMethod = this.value;
    let shippingFee;

    switch (shippingMethod) {
        case "express":
            shippingFee = 100000;
            break;
        case "same-day":
            shippingFee = 150000;
            break;
        default:
            shippingFee = 50000;
            break;
    }

    // Update the UI with the new shipping fee and total order price
    const totalProductPrice = parseFloat(document.getElementById("total-product-price").textContent.replace(/,/g, ''));
    const totalWithShipping = totalProductPrice + shippingFee;

    document.getElementById("total-shipping-fee").textContent = shippingFee.toLocaleString() + "đ";
    document.getElementById("total-order-price").textContent = totalWithShipping.toLocaleString() + "đ";
});

// Check session status periodically
let sessionCheckInterval = setInterval(function () {
    console.log('Checking session status...');
    fetch('/session-status')
        .then(response => response.json())
        .then(data => {
            // Redirect to homepage if session expired
            if (!data.sessionValid) {
                console.log('Session expired, redirecting to homepage...');
                clearInterval(sessionCheckInterval);
                window.location.href = '/';
            }
        })
        .catch(error => {
            console.error('Error checking session status:', error);
            clearInterval(sessionCheckInterval);
        });
}, 60000); // Check session every 60 seconds

// Stop session check when user leaves the page
window.addEventListener('beforeunload', function () {
    clearInterval(sessionCheckInterval);
});

// Check time remaining for checkout
function checkTimeRemaining() {
    fetch('/checkout-time-remaining')
        .then(response => response.json())
        .then(data => {
            // Show warning if less than 5 minutes remaining
            if (data.showWarning) {
                document.getElementById("timeoutWarning").style.display = "block";
            } else {
                document.getElementById("timeoutWarning").style.display = "none";
            }
        })
        .catch(error => console.error('Error checking time remaining:', error));
}

// Check remaining time every 10 seconds
setInterval(checkTimeRemaining, 10000);
document.getElementById("timeoutWarning").style.display = "none";

// Send cancel request when user navigates away from checkout page
window.addEventListener("beforeunload", function () {
    fetch("/checkout/cancel", {
        method: "POST",
        credentials: "same-origin" // Ensures the session is maintained
    }).catch(error => console.error("Error canceling checkout:", error));
});