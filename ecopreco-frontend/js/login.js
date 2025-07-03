// === login.js ===
// Este script trata o processo de login do usuário, realiza a autenticação via API e gerencia o redirecionamento com base no papel (role).

document.addEventListener("DOMContentLoaded", () => {
    // Seleciona o formulário de login pelo ID
    const form = document.getElementById("login-form");

    // Garante que o formulário exista antes de continuar
    if (form) {
        // Adiciona um listener para o envio do formulário
        form.addEventListener("submit", async (event) => {
            event.preventDefault(); // Impede o envio tradicional do formulário

            // Captura os valores digitados nos campos de login
            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            try {
                console.log("Tentando fazer login...");

                // Envia os dados para a API de autenticação
                const response = await apiRequest("/auth/login", "POST", { username, password });

                console.log("Resposta do servidor:", response);

                // Validação: verifica se a resposta contém os dados esperados
                if (!response || !response.token || !response.username) {
                    throw new Error("Resposta inválida do servidor.");
                }

                // Armazena o token JWT e o nome de usuário no localStorage
                localStorage.setItem("jwtToken", response.token);
                localStorage.setItem("username", response.username);

                // Armazena os papéis (roles) do usuário
                const userRoles = response.roles || ["ROLE_USER"];
                localStorage.setItem("userRoles", JSON.stringify(userRoles));

                // Exibe mensagem de sucesso
                showMessage("message", "Login bem-sucedido! Redirecionando...", "success");

                // Redireciona com base no papel do usuário
                setTimeout(() => {
                    if (userRoles.includes("ROLE_ADMIN")) {
                        window.location.href = "admin.html"; // Painel administrativo
                    } else {
                        window.location.href = "index.html"; // Página principal do usuário comum
                    }
                }, 1000);

            } catch (error) {
                console.error("Erro no login:", error.message);
                showMessage("message", error.message || "Erro ao fazer login.", "error");
            }
        });
    } else {
        console.warn("Formulário de login não encontrado.");
    }
});

// Função utilitária para fazer requisições para a API
async function apiRequest(endpoint, method, data) {
    const apiUrl = "http://localhost:8080"; // Altere se sua API estiver em outro lugar

    const response = await fetch(apiUrl + endpoint, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const message = errorData.message || "Erro desconhecido na requisição.";
        throw new Error(message);
    }

    return response.json();
}

// Função para exibir mensagens ao usuário
function showMessage(elementId, message, type = "info") {
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = message;
        el.style.display = "block";
        el.style.color = type === "success" ? "green" : type === "error" ? "red" : "black";
    }
}
