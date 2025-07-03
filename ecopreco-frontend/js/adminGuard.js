document.addEventListener("DOMContentLoaded", () => {
    // === Verificar se o usuário está logado ===
    const token = localStorage.getItem("jwtToken");
    const userRolesString = localStorage.getItem("userRoles");
    const username = localStorage.getItem("username");

    if (!token) {
        showMessage("message", "Você precisa estar logado para acessar esta página.", "error");
        setTimeout(() => window.location.href = "login.html", 3000);
        return;
    }

    try {
        const userRoles = userRolesString ? JSON.parse(userRolesString) : [];

        if (!userRoles.includes("ROLE_ADMIN")) {
            showMessage("message", "Acesso negado. Apenas administradores podem acessar esta página.", "error");
            setTimeout(() => window.location.href = "index.html", 3000);
            return;
        }

        console.log("Acesso permitido ao painel administrativo.");

        // Exibir nome de boas-vindas, se existir elemento
        const welcomeElement = document.getElementById("welcome-username");
        if (welcomeElement && username) {
            welcomeElement.textContent = username;
        }

    } catch (error) {
        console.error("Erro ao verificar permissões:", error);
        showMessage("message", "Erro ao verificar permissões. Faça login novamente.", "error");
        setTimeout(() => window.location.href = "login.html", 3000);
    }
});

// === Função para exibir mensagens ao usuário ===
function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    if (!element) return;

    element.textContent = message;
    element.className = `message ${type}`;
    element.style.display = "block";

    setTimeout(() => {
        element.style.display = "none";
    }, 5000);
}
