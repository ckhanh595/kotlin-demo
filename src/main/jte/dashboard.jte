@param String username
@param String email
@param String oauth2Provider
@param Boolean canManageUsers
@param org.springframework.security.web.csrf.CsrfToken csrf

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
    <div class="px-4 py-6 sm:px-0">
        <div class="bg-white shadow rounded-lg p-6">
            <h2 class="text-2xl font-bold mb-4">Welcome, ${username}</h2>

            @if(oauth2Provider != null)
                <p class="text-gray-600 mb-2">Login Method: ${oauth2Provider}</p>
            @endif

            <p class="text-gray-600 mb-4">Email: ${email}</p>

            @if(canManageUsers)
                <a href="/users"
                   class="inline-block mb-4 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                    Manage Users
                </a>
            @endif

            <div class="mt-4">
                <form action="/perform-logout" method="POST" class="inline">
                    <input type="hidden" name="${csrf.getParameterName()}" value="${csrf.getToken()}">
                    <button type="submit"
                            class="inline-block bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
                        Logout
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>