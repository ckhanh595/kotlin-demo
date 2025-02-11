@param String username
@param String email
@param String oauth2Provider

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="p-6">
<h2 class="text-2xl font-bold">Welcome, ${username}</h2>

@if(oauth2Provider != null)
    <p class="text-gray-600">Login Method: ${oauth2Provider}</p>
@endif

<p class="text-gray-600">Email: ${email}</p>


<a href="/logout" class="mt-4 block bg-red-500 text-white py-2 px-4">Logout</a>

</body>
</html>
