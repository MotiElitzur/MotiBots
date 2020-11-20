package moti.bots;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleHackerBot extends TelegramLongPollingBot {

    public static final String URL_REGEX = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
    
    
    enum ArticleSite{
        HAARETZ("haaretz"), THEMARKER("themarker"), GLOBES("globes"), YNET("ynet");

        String site = "";

        ArticleSite(String site) {

            this.site = site;
        }

        public String getSiteName() {
            return site;
        }
    }

    // region TelegramLongPollingBot

    public String getBotUsername() {

        return "ArticleHackerBot";
    }

    public String getBotToken() {
        return "956049940:AAFuEfehASHXRgkjqUQBY0nX0mx_wQ0sB08";
    }

    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            String userMessage = update.getMessage().getText();
            String botReplay = "";

            if (!isContainsUrl(userMessage) || !isValidSite(userMessage)) {

                botReplay = "קישור לא זוהה";
            } else if(userMessage.contains("/amp/")){

                botReplay = "הכתבה פרוצה כבר";
            } else if(userMessage.contains(ArticleSite.HAARETZ.getSiteName()) ||
                    userMessage.contains(ArticleSite.THEMARKER.getSiteName())){

                String urlDomain = getDomain(userMessage);

                String[] messageParts = userMessage.split(urlDomain);

                botReplay = messageParts[0] + urlDomain + "/amp" + messageParts[1];
            }

            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setReplyToMessageId(update.getMessage().getMessageId());
            message.setText(botReplay);

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    // endregion

    private boolean isContainsUrl(String text){


        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(text);//replace with string to compare

        return matcher.find();
    }

    private boolean isValidSite(String stringToSearch){

        boolean isTextContainsValidSite = false;

        for (ArticleSite site: ArticleSite.values()) {

            if (stringToSearch.contains(site.getSiteName())){

                isTextContainsValidSite = true;

                break;
            }
        }

        return isTextContainsValidSite;
    }

    private String getDomain(String url){

            return url.contains(".com") ? ".com" : ".co.il";
    }
}
