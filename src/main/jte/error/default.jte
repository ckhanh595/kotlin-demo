@param String errorTitle
@param String errorMessage


<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="p-6">
    <h2 class="text-2xl font-bold text-red-600">${errorTitle}</h2>
    <p class="text-gray-600">${errorMessage}</p>
    <a href="/" class="mt-4 inline-block bg-blue-500 text-white py-2 px-4 rounded">Go Home</a>
</body>
</html>
