@param java.util.List<com.sts.demo.entity.UserEntity> users
@param com.sts.demo.model.enums.UserRole[] roles
@param String error
@param String success
@param org.springframework.security.web.csrf.CsrfToken csrf

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
    <div class="px-4 py-6 sm:px-0">
        <!-- Messages -->
        @if(error != null)
            <div class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                ${error}
            </div>
        @endif
        @if(success != null)
            <div class="mb-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded">
                ${success}
            </div>
        @endif

        <!-- Create User Form -->
        <div class="bg-white shadow rounded-lg p-6 mb-6">
            <h2 class="text-xl font-bold mb-4">Create New User</h2>
            <form action="/users/create" method="POST" class="space-y-4">
                <!-- Add CSRF token -->
                <input type="hidden" name="${csrf.getParameterName()}" value="${csrf.getToken()}">

                <div>
                    <label class="block text-sm font-medium text-gray-700">Username</label>
                    <input type="text" name="username" required
                           class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Email</label>
                    <input type="email" name="email" required
                           class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Password</label>
                    <input type="password" name="password" required
                           class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Role</label>
                    <select name="role" required
                            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500">
                        @for(var role : roles)
                            <option value="${role}">${role}</option>
                        @endfor
                    </select>
                </div>
                <button type="submit"
                        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                    Create User
                </button>
            </form>
        </div>

        <!-- Users Table -->
        <div class="bg-white shadow rounded-lg p-6">
            <h2 class="text-xl font-bold mb-4">User List</h2>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Username</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Provider</th>
                    </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                    @for(var user : users)
                        <tr>
                            <td class="px-6 py-4 whitespace-nowrap">${user.getUsername()}</td>
                            <td class="px-6 py-4 whitespace-nowrap">${user.getEmail() == null? "" : user.getEmail()}</td>
                            <td class="px-6 py-4 whitespace-nowrap">${user.getUserRole()}</td>
                            <td class="px-6 py-4 whitespace-nowrap">${user.getOauth2Provider() == null? "Local" : user.getOauth2Provider().toString()}</td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Back to Dashboard Button -->
        <div class="mt-6">
            <a href="/dashboard"
               class="inline-block bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">
                Back to Dashboard
            </a>
        </div>
    </div>
</div>
</body>
</html>