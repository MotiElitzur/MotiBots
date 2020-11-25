package moti.bots.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public abstract class UpdateManagerBot implements UpdatesListener {

    // region Data Members

    private String botToken;

    private String botUserName;

    BotType botType;

    private TelegramBot bot;

    private boolean isAlreadyAddListener = false;

    // endregion

    // region Constructor

    public UpdateManagerBot(String botToken, String botUserName, BotType botType) {

        this.botToken = botToken;
        this.botUserName = botUserName;
        this.botType = botType;

        bot = new TelegramBot(this.botToken);
    }

    // endregion

    // region Properties

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getBotUserName() {
        return botUserName;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public BotType getBotType() {
        return botType;
    }

    public void setBotType(BotType botType) {
        this.botType = botType;
    }

    public TelegramBot getBot() {
        return bot;
    }

    public void setBot(TelegramBot bot) {
        this.bot = bot;
    }

    // endregion

    // region Public Methods

    public void start(){

        if(!isAlreadyAddListener){

            bot.setUpdatesListener(this);

            isAlreadyAddListener = true;
        }
    }

    public void stop(){

        bot.removeGetUpdatesListener();

        isAlreadyAddListener = false;
    }

    // endregion

    // region Abstract Methods

    public abstract void textUpdate(Message message);

    // endregion

    // region UpdatesListener

    @Override
    public int process(List<Update> updates) {

        int isBotSuccess = UpdatesListener.CONFIRMED_UPDATES_NONE;

        for (Update update: updates) {

            switch (botType){

                case TEXT:

                    // Check that the update is text.
                    if(update.message().text() != null){

                        textUpdate(update.message());

                        isBotSuccess = UpdatesListener.CONFIRMED_UPDATES_ALL;
                    }

                    break;
                case File:

                    break;
                default:
                    break;
            }
        }

        return isBotSuccess;
    }

    // endregion
}
