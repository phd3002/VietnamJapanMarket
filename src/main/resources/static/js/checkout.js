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