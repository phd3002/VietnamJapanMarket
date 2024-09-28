document.addEventListener('DOMContentLoaded', function () {
    const notificationLink = document.querySelector('.notification-link');
    const notificationDropdown = document.querySelector('.notification-dropdown');

    // Khi người dùng click vào icon notification thì chuyển trang
    notificationLink.addEventListener('click', function (event) {
        event.preventDefault(); // Để thử nghiệm (có thể bỏ khi không cần)
        window.location.href = 'notifications.html'; // Điều hướng tới trang thông báo
    });

    // Đảm bảo dropdown hiển thị khi hover
    const notification = document.querySelector('.notification');
    notification.addEventListener('mouseover', function () {
        notificationDropdown.style.display = 'block';
    });

    notification.addEventListener('mouseout', function () {
        notificationDropdown.style.display = 'none';
    });
});

document.addEventListener('DOMContentLoaded', function () {
    const orderList = document.querySelector('#nav-orders');
    const orderDetail = document.querySelector('#nav-order-detail');
    const viewButtons = document.querySelectorAll('.view-btn');
    const tabs = document.querySelectorAll('.nav-item.nav-link');

    // Khi bấm vào nút "View", hiển thị bảng Order Detail
    viewButtons.forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();
            const orderId = this.getAttribute('data-order-id');

            // Chỉ hiển thị Order Detail nếu tab Orders đang được chọn
            if (orderList.classList.contains('active')) {
                showOrderDetail(orderId);
            }
            e.stopPropagation(); // Ngăn chặn sự kiện click lan tới document
        });
    });

    // Sự kiện click trên document để ẩn Order Detail khi bấm ngoài vùng Order Detail
    document.addEventListener('click', function (e) {
        // Chỉ ẩn Order Detail nếu người dùng đang ở tab Orders
        if (orderList.classList.contains('active') && !orderDetail.contains(e.target)) {
            hideOrderDetail();
        }
    });

    // Ngăn chặn click vào bên trong Order Detail làm nó bị ẩn đi
    orderDetail.addEventListener('click', function (e) {
        e.stopPropagation(); // Ngăn không cho sự kiện click lan tới document
    });

    // Hiển thị Order Detail và cập nhật nội dung
    function showOrderDetail(orderId) {
        orderList.classList.remove('active', 'show');
        orderDetail.classList.add('active', 'show');
        document.getElementById('order-id').textContent = orderId;
    }

    // Ẩn Order Detail và quay lại danh sách
    function hideOrderDetail() {
        orderDetail.classList.remove('active', 'show');
        orderList.classList.add('active', 'show');
    }

    // Thêm sự kiện vào các tab để chỉ hiển thị bảng Order trong tab Orders
    tabs.forEach(tab => {
        tab.addEventListener('click', function () {
            // Nếu chuyển sang tab khác ngoài Orders, ẩn Order và Order Detail
            if (this.getAttribute('href') !== '#nav-orders') {
                hideOrderDetail(); // Ẩn Order Detail
                orderList.classList.remove('active', 'show'); // Ẩn Order List
            } else {
                orderList.classList.add('active', 'show'); // Hiển thị Order List nếu ở tab Orders
            }
        });
    });
});

document.addEventListener('DOMContentLoaded', function () {
    // Function to show a specific tab
    function showTab(tabId) {
        // Hide all tab panes
        document.querySelectorAll('.tab-pane').forEach(function (pane) {
            pane.classList.remove('active', 'show');
        });
        // Show the selected tab pane
        var selectedPane = document.querySelector(tabId);
        if (selectedPane) {
            selectedPane.classList.add('active', 'show');
        }

        // Update nav tabs
        document.querySelectorAll('.nav-link').forEach(function (link) {
            link.classList.remove('active');
            if (link.getAttribute('href') === tabId) {
                link.classList.add('active');
            }
        });
    }

    // Check if URL has a hash (like #nav-notification)
    if (window.location.hash) {
        const tabId = window.location.hash;
        showTab(tabId);  // Automatically switch to the tab in the hash
    }

    // Listen for hash changes (if user manually changes hash or clicks anchor links)
    window.addEventListener('hashchange', function () {
        const tabId = window.location.hash;
        showTab(tabId);
    });

    // Event listener for "View Details" in Notifications
    document.querySelectorAll('.view-order').forEach(function (link) {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            var orderId = this.getAttribute('data-order-id');

            // Assuming you have an element with id="order-id" in the Order Detail section
            var orderElement = document.getElementById('order-id');
            if (orderElement) {
                orderElement.textContent = "Order #" + orderId;

                // Set the hash to #nav-order-detail
                window.location.hash = '#nav-order-detail';

                // Optionally, load more order details via AJAX here
            }
        });
    });
});

//order detail progress
function updateOrderProgress(currentStep) {
    const steps = document.querySelectorAll('.order-progress-step');

    steps.forEach((step, index) => {
        if (index < currentStep) {
            step.classList.add('completed');
            step.classList.remove('active');
        } else if (index === currentStep) {
            step.classList.add('active');
        } else {
            step.classList.remove('completed', 'active');
        }
    });
}

updateOrderProgress(2);
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
//popup save change
document.getElementById('saveChangesBtn').addEventListener('click', function (e) {
    e.preventDefault(); // Ngăn không cho form submit mặc định

    // Hiển thị popup thành công và overlay
    document.getElementById('successPopup').style.display = 'block';
    document.getElementById('popupOverlay').style.display = 'block';
});

document.getElementById('saveChangesShop').addEventListener('click', function (e) {
    e.preventDefault(); // Ngăn không cho form submit mặc định

    // Hiển thị popup thành công và overlay
    document.getElementById('successPopup').style.display = 'block';
    document.getElementById('popupOverlay').style.display = 'block';
});

document.getElementById('popupOkBtn').addEventListener('click', function () {
    // Ẩn popup và overlay
    document.getElementById('successPopup').style.display = 'none';
    document.getElementById('popupOverlay').style.display = 'none';

    // Reload lại trang
    location.reload();
});
//popup add address
document.querySelector('.new-address-btn').addEventListener('click', function() {
    document.getElementById('newAddressPopup').style.display = 'block';
    document.getElementById('popupOverlay').style.display = 'block';
});

// Sự kiện khi nhấn "ADD" trong popup thêm địa chỉ mới
document.getElementById('addAddressBtn').addEventListener('click', function() {
    // Ẩn popup thêm địa chỉ mới
    document.getElementById('newAddressPopup').style.display = 'none';
    document.getElementById('popupOverlay').style.display = 'none';

    // Hiển thị popup thành công
    document.getElementById('successPopup').style.display = 'block';
});

// Sự kiện Cancel trong popup
document.getElementById('cancelBtn').addEventListener('click', function() {
    document.getElementById('newAddressPopup').style.display = 'none';
    document.getElementById('popupOverlay').style.display = 'none';
});

// Sự kiện khi nhấn Update (tương tự thêm mới, bạn có thể điều chỉnh theo yêu cầu)
document.querySelectorAll('.address-actions a').forEach(function(element) {
    if (element.textContent === 'Cập nhật' || element.textContent === 'Update') {
        element.addEventListener('click', function() {
            document.getElementById('newAddressPopup').style.display = 'block';
            document.getElementById('popupOverlay').style.display = 'block';
        });
    }
});


