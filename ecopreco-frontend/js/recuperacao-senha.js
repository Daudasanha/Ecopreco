// Espera o carregamento completo do DOM antes de executar o código
document.addEventListener("DOMContentLoaded", () => {
    // Obtém o formulário de recuperação de senha pelo seu ID
    const forgotPasswordForm = document.getElementById("forgot-password-form");

    // Verifica se o formulário existe na página para evitar erros
    if (forgotPasswordForm) {
        // Adiciona um listener para o evento de submissão do formulário
        forgotPasswordForm.addEventListener("submit", async (event) => {
            event.preventDefault(); // Previne o comportamento padrão (recarregar a página)

            // Obtém o valor do campo de email preenchido pelo usuário
            const email = document.getElementById("email").value;

            try {
                // Aqui deveria ser feita a requisição para o backend para iniciar a recuperação de senha
                // Porém, esse endpoint ainda não foi implementado, então a chamada está comentada
                // await apiRequest("/auth/forgot-password", "POST", { email });

                // Exibe mensagem de sucesso informando que, se o email estiver cadastrado,
                // será enviado um link para redefinir a senha
                showMessage("message", "Se o email estiver cadastrado, um link de redefinição será enviado.", "success");

                // Limpa o formulário após envio
                forgotPasswordForm.reset();
            } catch (error) {
                // Caso ocorra algum erro, exibe no console para debug
                console.error("Erro na recuperação de senha:", error);

                // Exibe mensagem de erro para o usuário, utilizando a mensagem do erro
                // ou uma mensagem padrão caso não tenha uma específica
                showMessage("message", error.message || "Erro ao solicitar recuperação de senha.", "error");
            }
        });
    }
});
