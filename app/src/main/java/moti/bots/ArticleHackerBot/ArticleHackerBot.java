package moti.bots.ArticleHackerBot;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moti.bots.bots.BotType;
import moti.bots.bots.UpdateManagerBot;

public class ArticleHackerBot extends UpdateManagerBot {

    // region enum

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

    // endregion

    // region Constants

    private static final String BOT_TOKEN = "956049940:AAFuEfehASHXRgkjqUQBY0nX0mx_wQ0sB08";

    private static final String BOT_USERNAME = "ArticleHackerBot";

    public static final String URL_REGEX =
            "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";

    private final  String HACK_CODE = "/amp";

    // endregion

    // region Data Members

    private String  replayMessage = "";

    // endregion

    // region Singleton
    
    private static final ArticleHackerBot INSTANCE = new ArticleHackerBot();

    public static ArticleHackerBot getInstance() {
        return INSTANCE;
    }

    // endregion

    // region Constructor

    private ArticleHackerBot() {

        super(BOT_TOKEN, BOT_USERNAME, BotType.TEXT);
    }

    // endregion

    // region Properties

    public String getReplayMessage() {
        return replayMessage;
    }

    public void setReplayMessage(String replayMessage) {
        this.replayMessage = replayMessage;
    }

    // endregion

    // region Private Methods

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

    private String splitAndAdd(String originalText ,String splitText, String textToAddBetween){

        String resultText = "";

        String[] textParts = originalText.split(splitText);

        for (int index = 0; index < textParts.length - 1; index++) {

            resultText += textParts[index] + splitText + textToAddBetween + textParts[index + 1];
        }

        return resultText;
    }


    private ArticleSite getSite(String userMessage){

        ArticleSite articleSite = null;

        for (ArticleSite site: ArticleSite.values()) {

            if (userMessage.contains(site.getSiteName())){

                articleSite = site;

                break;
            }
        }

        return articleSite;
    }

    private boolean recogniseCommandsAndErrors(Message message) {

        boolean isRecogniseCommandsOrError = false;
        String userMessage = message.text();

        if (!isContainsUrl(userMessage) || !isValidSite(userMessage)) {

            replayMessage = "קישור לא זוהה";
            isRecogniseCommandsOrError = true;
        } else if(userMessage.contains(HACK_CODE)){

            replayMessage = "הכתבה פרוצה כבר";
            isRecogniseCommandsOrError = true;
        }


        if (userMessage.equals("/start")) {

            replayMessage = "שלום ";
            replayMessage += message.chat().firstName() + " \uD83D\uDC4B\n\n";
            replayMessage += "הבוט פורץ כתבות נעולות לקריאה\n" +
                    "מהאתרים \"הארץ\" ו \"Themarker\".\n\n" +
                    "יש לשלוח לבוט קישור לכתבה והוא יחזיר קישור פרוץ.";

            isRecogniseCommandsOrError = true;
        } else if(userMessage.contains("עזרה") ||
                userMessage.equalsIgnoreCase("/help")){

            replayMessage = "הכתבה פרוצה כבר";
            isRecogniseCommandsOrError = true;
        }

        return isRecogniseCommandsOrError;
    }

    // endregion

    // region TextManipulatorBot

    @Override
    public void textUpdate(Message message) {

        String userMessage = message.text();

        if (!recogniseCommandsAndErrors(message)){

            switch (getSite(userMessage)){

                case HAARETZ:
                case THEMARKER:

                    replayMessage = splitAndAdd(userMessage, getDomain(userMessage), HACK_CODE);

                    break;
                case GLOBES:

                    break;

                case YNET:

                    break;

                default:

                    break;
            }
        }

        SendMessage request = new SendMessage(message.chat().id(), replayMessage)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true)
                .replyToMessageId(message.messageId());

        getBot().execute(request);
    }

    // endregion
}