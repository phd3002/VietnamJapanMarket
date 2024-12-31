// Function to round to the nearest multiple (e.g., 1000 or 100)
function roundToNearest(value, multiple) {
    return Math.round(value / multiple) * multiple;
}

document.addEventListener('DOMContentLoaded', function() {
    const paymentMethodSelect = document.getElementById('paymentMethodSelect');
    const paymentTypeSelect = document.getElementById('paymentTypeSelect');
    const walletDetails = document.getElementById('walletDetails');
    const vnpayDetails = document.getElementById('vnpayDetails');
    const paymentMethodInput = document.getElementById('paymentMethod');
    const paymentTypeInput = document.getElementById('paymentType');

    // Handle payment method change
    paymentMethodSelect.addEventListener('change', function() {
        const selectedMethod = this.value;
        paymentMethodInput.value = selectedMethod;

        // Toggle display of payment details
        if (selectedMethod === 'WALLET') {
            walletDetails.style.display = 'block';
            vnpayDetails.style.display = 'none';
        } else {
            walletDetails.style.display = 'none';
            vnpayDetails.style.display = 'block';
        }

        // Update total amount when payment method changes
        updateTotalAmount();
    });

    // Handle payment type change
    paymentTypeSelect.addEventListener('change', function() {
        paymentTypeInput.value = this.value;
        updateTotalAmount();
    });

    // Update the total shipping fee and total order price when shipping method changes
    document.getElementById("shipping-method").addEventListener("change", function() {
        updateShippingAndTotal();
    });

    // Function to update shipping fee and recalculate total
    function updateShippingAndTotal() {
        const shippingSelect = document.getElementById("shipping-method");
        const selectedOption = shippingSelect.options[shippingSelect.selectedIndex];
        const shippingFeePerKg = parseFloat(selectedOption.getAttribute("data-fee"));
        const totalWeight = parseFloat(document.getElementById("total-weight").textContent);

        // Calculate total shipping fee
        let shippingFee = shippingFeePerKg * totalWeight;
        shippingFee = roundToNearest(shippingFee, 1000);

        // Update shipping fee display
        document.getElementById("total-shipping-fee").textContent = shippingFee.toLocaleString() + " VND";

        // Update total amount after shipping fee changes
        updateTotalAmount();
    }

    // Function to update total amount based on all selections
    function updateTotalAmount() {
        const totalProductPrice = parseFloat(document.getElementById("total-product-price").textContent.replace(/[^\d]/g, ''));
        const shippingFee = parseFloat(document.getElementById("total-shipping-fee").textContent.replace(/[^\d]/g, ''));
        const taxRate = 0.08;

        // Calculate the tax on the product price
        const taxAmount = totalProductPrice * taxRate;
        document.getElementById("total-tax-fee").textContent = taxAmount.toLocaleString() + " VND";

        // Get the selected payment type
        const paymentType = paymentTypeSelect.value;

        let finalOrderTotal;
        if (paymentType === "deposit") {
            // For deposit, calculate 50% of product price + full shipping + full tax
            finalOrderTotal = (totalProductPrice * 0.5) + shippingFee + taxAmount;
        } else {
            // For full payment, include entire product price + shipping + tax
            finalOrderTotal = totalProductPrice + shippingFee + taxAmount;
        }

        // Update the displayed total amount
        document.getElementById("total-order-price").textContent = finalOrderTotal.toLocaleString() + " VND";
    }

    // Initialize total amount on page load
    updateShippingAndTotal();
});