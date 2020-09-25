package ro.catalin.console;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.catalin.Game;
import ro.catalin.MessageGenerator;

import java.util.Scanner;

@Component
public class ConsoleNumberGuess {

    // == fields ==
    private final Game game;

    private final MessageGenerator messageGenerator;

    // == constructors ==
    public ConsoleNumberGuess(Game game, MessageGenerator messageGenerator) {
        this.game = game;
        this.messageGenerator = messageGenerator;
    }

    // == events ==
    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(messageGenerator.getMainMessage());
            System.out.println(messageGenerator.getResultMessage());

            int guess = scanner.nextInt();
            scanner.nextLine();
            game.setGuess(guess);
            game.check();

            if (game.isGameWon() || game.isGameLost()) {
                System.out.println(messageGenerator.getResultMessage());
                System.out.println("Joci din nou? DA / NU");

                String playAgainString = scanner.nextLine().trim();
                if (!playAgainString.equalsIgnoreCase("da")) {
                    break;
                }
                game.reset();
            }
        }
    }
}
