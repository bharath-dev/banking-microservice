const API_URL = '/api/customers';

document.addEventListener('DOMContentLoaded', loadCustomers);

async function loadCustomers() {
    try {
        const response = await fetch(API_URL);
        const customers = await response.json();
        renderTable(customers);
    } catch (error) {
        showApiResponse(500, { error: 'Failed to load customers from server', message: error.message });
    }
}

// Resets search bar, clears any error, and fetches all customers
async function fetchAllCustomers() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) searchInput.value = '';
    clearError();
    await loadCustomers();
}

async function searchCustomers() {
    const city = document.getElementById('searchInput').value.trim();
    if (!city) {
        // If empty search box, load all customers
        loadCustomers();
        return;
    }
    try {
        const response = await fetch(`${API_URL}?city=${encodeURIComponent(city)}`);
        const customers = await response.json();
        renderTable(customers, city);
    } catch (error) {
        showApiResponse(500, { error: 'Search failed for city: ' + city, message: error.message });
    }
}

async function handleFormSubmit(e) {
    e.preventDefault();
    clearError();

    const id = document.getElementById('customerId').value;

    const balanceVal = document.getElementById('balance').value;

    const customer = {
        firstName: document.getElementById('firstName').value.trim(),
        lastName: document.getElementById('lastName').value.trim(),
        email: document.getElementById('email').value.trim(),
        phoneNumber: document.getElementById('phone').value.trim(),
        nationalId: document.getElementById('nationalId').value.trim(),
        city: document.getElementById('city').value.trim(),
        address: document.getElementById('address').value.trim(),
        accountStatus: document.getElementById('status').value,
        accountBalance: balanceVal !== '' ? parseFloat(balanceVal) : null
    };

    try {
        let response;
        if (id) {
            // Updating existing customer
            response = await fetch(`${API_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(customer)
            });
        } else {
            // Creating new customer
            response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(customer)
            });
        }

        if (!response.ok) {
            let errData;
            try {
                errData = await response.json();
            } catch (e) {
                errData = { error: 'HTTP ' + response.status, message: await response.text() };
            }
            showApiResponse(response.status, errData);
            return;
        }

        clearForm();
        loadCustomers();
    } catch (error) {
        showApiResponse(500, { error: 'Request Failed', message: error.message });
    }
}

async function deleteCustomer(id) {
    if(!confirm('Are you sure you want to delete customer #' + id + '?')) return;
    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete');
        loadCustomers();
    } catch (error) {
        showError('Failed to delete customer.');
    }
}

function editCustomer(customerStr) {
    clearError();
    const c = JSON.parse(decodeURIComponent(customerStr));
    document.getElementById('customerId').value = c.id;
    document.getElementById('firstName').value = c.firstName;
    document.getElementById('lastName').value = c.lastName;
    document.getElementById('email').value = c.email;
    document.getElementById('phone').value = c.phoneNumber;
    document.getElementById('nationalId').value = c.nationalId;
    document.getElementById('city').value = c.city || '';
    document.getElementById('address').value = c.address || '';
    document.getElementById('status').value = c.accountStatus;
    document.getElementById('balance').value = c.accountBalance;
    document.getElementById('submitBtn').textContent = 'Update Customer (ID: ' + c.id + ')';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function clearForm() {
    document.getElementById('customerForm').reset();
    document.getElementById('customerId').value = '';
    document.getElementById('submitBtn').textContent = 'Save Customer';
    clearError();
}

function renderTable(customers, searchedCity = null) {
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '';

    const countSpan = document.getElementById('customerCount');
    if (countSpan) {
        countSpan.textContent = Array.isArray(customers) ? customers.length : 0;
    }

    if (!Array.isArray(customers) || customers.length === 0) {
        const msg = searchedCity
            ? `No customers found living in city: "${searchedCity}". (Note: Search matches the City column).`
            : 'No customers found in database. Add a customer using the form above.';
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: #6b7280; padding: 24px; font-style: italic;">${msg}</td></tr>`;
        return;
    }

    customers.forEach(c => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>#${c.id}</strong></td>
            <td>${c.firstName} ${c.lastName}</td>
            <td>${c.email}</td>
            <td>${c.city || '<span style="color:#9ca3af">N/A</span>'}</td>
            <td><span class="status-badge status-${c.accountStatus}">${c.accountStatus}</span></td>
            <td>$${Number(c.accountBalance).toFixed(2)}</td>
            <td>
                <button class="btn-edit" onclick="editCustomer('${encodeURIComponent(JSON.stringify(c))}')">Edit</button>
                <button class="btn-danger" onclick="deleteCustomer(${c.id})">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Displays both parsed validation errors AND the exact raw API JSON response
function showApiResponse(statusCode, responseData) {
    const errorBox = document.getElementById('errorBox');
    if (!errorBox) return;

    let title = `HTTP ${statusCode} - ${responseData.error || 'Request Rejected'}`;
    let fieldErrors = [];

    // Handles Spring Boot validation errors (e.g. { message: { lastName: 'Last name is required', ... } })
    if (responseData.message && typeof responseData.message === 'object') {
        fieldErrors = Object.entries(responseData.message).map(([field, msg]) => `<strong>${field}</strong>: ${msg}`);
    } else if (responseData.errors && typeof responseData.errors === 'object') {
        fieldErrors = Object.entries(responseData.errors).map(([field, msg]) => `<strong>${field}</strong>: ${msg}`);
    } else if (typeof responseData.message === 'string') {
        fieldErrors.push(responseData.message);
    }

    const rawJson = JSON.stringify(responseData, null, 2);

    let html = `
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #fca5a5; padding-bottom: 8px; margin-bottom: 10px;">
            <div style="color: #b91c1c; font-weight: 700; font-size: 14px;">❌ ${title}</div>
            <span style="background: #fee2e2; color: #991b1b; padding: 2px 8px; border-radius: 4px; font-family: monospace; font-size: 12px; font-weight: 600;">Status: ${statusCode}</span>
        </div>
    `;

    if (fieldErrors.length > 0) {
        html += '<div style="font-weight: 600; color: #7f1d1d; font-size: 13px; margin-bottom: 6px;">Field Validation Issues:</div>';
        html += '<ul style="margin: 0 0 12px 20px; color: #991b1b; font-size: 13px; list-style-type: disc;">';
        fieldErrors.forEach(item => {
            html += `<li style="margin-bottom: 4px;">${item}</li>`;
        });
        html += '</ul>';
    }

    html += `
        <div style="margin-top: 10px;">
            <div style="font-size: 11px; font-weight: 700; color: #6b7280; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 4px;">Raw API Response (As returned from backend):</div>
            <pre style="background: #0f172a; color: #38bdf8; padding: 12px; border-radius: 6px; font-family: monospace; font-size: 12px; overflow-x: auto; margin: 0; white-space: pre-wrap; line-height: 1.5; border: 1px solid #1e293b;"><code>${rawJson}</code></pre>
        </div>
    `;

    errorBox.innerHTML = html;
    errorBox.style.display = 'block';
    errorBox.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function clearError() {
    const errorBox = document.getElementById('errorBox');
    if (errorBox) {
        errorBox.innerHTML = '';
        errorBox.style.display = 'none';
    }
}