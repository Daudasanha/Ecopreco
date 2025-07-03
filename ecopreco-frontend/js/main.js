// === app.js ===
// Script principal de configuração global, autenticação e controle de navegação.

// =======================
// Configurações Globais
// =======================
const API_BASE_URL = "http://localhost:8080/api";

// =======================
// Exibir mensagens de feedback
// =======================
function showMessage(elementId, message, type) {
    const messageElement = document.getElementById(elementId);
    if (messageElement) {
        messageElement.textContent = message;
        messageElement.className = `message ${type}`;
        messageElement.style.display = "block";
        setTimeout(() => {
            messageElement.style.display = "none";
        }, 5000); // Oculta a mensagem após 5 segundos
    }
}

// =======================
// Função genérica para requisições à API
// =======================
async function apiRequest(url, method = "GET", data = null, authRequired = false) {
    const headers = {
        "Content-Type": "application/json",
    };

    if (authRequired) {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
            throw new Error("Token de autenticação não encontrado.");
        }
        headers["Authorization"] = `Bearer ${token}`;
    }

    const options = {
        method,
        headers,
    };

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, options);

        // Tratamento de erros
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `Erro na requisição: ${response.statusText}`);
        }

        // Caso a resposta seja 204 No Content
        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error("Erro na requisição API:", error);
        throw error;
    }
}

// =======================
// Botões de redirecionamento (Login e Cadastro)
// =======================
document.addEventListener("DOMContentLoaded", () => {
    const loginBtn = document.querySelector(".btn-login");
    const registerBtn = document.querySelector(".btn-register");

    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            window.location.href = "login.html";
        });
    }

    if (registerBtn) {
        registerBtn.addEventListener("click", () => {
            window.location.href = "cadastro.html";
        });
    }
});

// =======================
// Verifica se o usuário está logado
// =======================
function isLoggedIn() {
    const token = localStorage.getItem("jwtToken");
    return !!token;
}

// =======================
// Faz logout do usuário
// =======================
function logout() {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("username");
    localStorage.removeItem("userRoles");
    window.location.href = "index.html"; // Redireciona após logout
}

// =======================
// Atualiza a barra de navegação com base na autenticação
// =======================
document.addEventListener("DOMContentLoaded", () => {
    const authButtonsDiv = document.querySelector(".auth-buttons");

    if (authButtonsDiv) {
        if (isLoggedIn()) {
            // Usuário logado
            const username = localStorage.getItem("username");
            const userRoles = JSON.parse(localStorage.getItem("userRoles"));

            authButtonsDiv.innerHTML = `
                <span>Olá, ${username}!</span>
                <button class="btn-logout">Sair</button>
            `;

            // Evento de logout
            const logoutBtn = authButtonsDiv.querySelector(".btn-logout");
            if (logoutBtn) {
                logoutBtn.addEventListener("click", logout);
            }

            // Exibe link para o painel admin, se for admin
            if (userRoles && userRoles.includes("ROLE_ADMIN")) {
                const navLinks = document.querySelector(".nav-links");
                if (navLinks && !navLinks.querySelector(".admin-link")) {
                    const adminLink = document.createElement("li");
                    adminLink.innerHTML = `<a href="admin.html" class="admin-link">Admin</a>`;
                    navLinks.appendChild(adminLink);
                }
            }

        } else {
            // Usuário não logado
            authButtonsDiv.innerHTML = `
                <button class="btn-login">Login</button>
                <button class="btn-register btn-secondary">Cadastre-se</button>
            `;

            // Eventos de redirecionamento
            const loginBtn = authButtonsDiv.querySelector(".btn-login");
            const registerBtn = authButtonsDiv.querySelector(".btn-register");

            if (loginBtn) {
                loginBtn.addEventListener("click", () => {
                    window.location.href = "login.html";
                });
            }

            if (registerBtn) {
                registerBtn.addEventListener("click", () => {
                    window.location.href = "cadastro.html";
                });
            }
        }
    }
});
