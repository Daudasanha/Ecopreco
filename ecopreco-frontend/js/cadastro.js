// === cadastro.js ===
// Este script trata o envio do formulário de cadastro de novo usuário.

document.addEventListener("DOMContentLoaded", () => {
    // Obtém o formulário de cadastro
    const registerForm = document.getElementById("register-form");

    // Verifica se o formulário existe na página
    if (registerForm) {
        // Adiciona um listener para o evento de envio do formulário
        registerForm.addEventListener("submit", async (event) => {
            // Evita o comportamento padrão de recarregar a página
            event.preventDefault();

            // Captura os valores dos campos do formulário
            const fullName = document.getElementById("fullName").value;
            const username = document.getElementById("username").value;
            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;

            try {
                // Faz a requisição POST para a API de registro
                const response = await apiRequest("/auth/register", "POST", {
                    fullName,
                    username,
                    email,
                    password
                });

                // Exibe mensagem de sucesso e limpa o formulário
                showMessage("message", response.message, "success");
                registerForm.reset();

            } catch (error) {
                // Em caso de erro, exibe mensagem de erro
                console.error("Erro no cadastro:", error);
                showMessage("message", error.message || "Erro ao cadastrar. Tente novamente.", "error");
            }
        });
    }
});
