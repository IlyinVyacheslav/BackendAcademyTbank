package backend.academy.bot.exceptions;

import lombok.Getter;

@Getter
public class IllegalCommandException extends IllegalArgumentException {
    private String chatId;

    public IllegalCommandException(String chatId) {
        this.chatId = chatId;
    }
}
