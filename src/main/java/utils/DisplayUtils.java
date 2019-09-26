package utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.display.BackButtonBehavior;
import com.amazon.ask.model.interfaces.display.BodyTemplate1;
import com.amazon.ask.model.interfaces.display.BodyTemplate2;
import com.amazon.ask.model.interfaces.display.BodyTemplate3;
import com.amazon.ask.model.interfaces.display.BodyTemplate6;
import com.amazon.ask.model.interfaces.display.Image;
import com.amazon.ask.model.interfaces.display.ImageInstance;
import com.amazon.ask.model.interfaces.display.ListItem;
import com.amazon.ask.model.interfaces.display.ListTemplate1;
import com.amazon.ask.model.interfaces.display.ListTemplate2;
import com.amazon.ask.model.interfaces.display.PlainText;
import com.amazon.ask.model.interfaces.display.RichText;
import com.amazon.ask.model.interfaces.display.Template;
import com.amazon.ask.model.interfaces.display.TextContent;
import com.amazon.ask.model.interfaces.viewport.Experience;
import com.amazon.ask.model.interfaces.viewport.Shape;
import com.amazon.ask.model.ui.Card;
import com.amazon.ask.response.ResponseBuilder;
import com.amazon.ask.response.TextContentHelper;

public class DisplayUtils {
	// TODO: Uncomment logger
	// private final static Logger logger = Logger.getLogger(DisplayUtils.class);

	private DisplayUtils() {
	}

	/**
	 * This is a helper method to log display stats
	 * {@code token , pxHeight, pxWidth, curPxWidth, curPxHeight, DPI, getShape, getExperiences}
	 * 
	 * @param input
	 */
	public static void displayStats(HandlerInput input) {
		StringBuffer strBuf = new StringBuffer("\nDisplay Properties\n");
		String token = input.getRequestEnvelope().getContext().getDisplay().getToken();
		strBuf.append(String.format("token : %s\n", token));
		BigDecimal pxHeight = input.getRequestEnvelope().getContext().getViewport().getPixelHeight();
		strBuf.append(String.format("pixel height : %s\n", pxHeight));
		BigDecimal pxWidth = input.getRequestEnvelope().getContext().getViewport().getPixelWidth();
		strBuf.append(String.format("pixel width : %s\n", pxWidth));
		BigDecimal curPxHeight = input.getRequestEnvelope().getContext().getViewport().getCurrentPixelHeight();
		strBuf.append(String.format("current pixel height : %s\n", curPxHeight));
		BigDecimal curPxWidth = input.getRequestEnvelope().getContext().getViewport().getDpi();
		strBuf.append(String.format("current pixel width : %s\n", curPxWidth));
		Shape shape = input.getRequestEnvelope().getContext().getViewport().getShape();
		strBuf.append(String.format("shape : %s\n", shape.toString()));
		strBuf.append("Experiences\n");
		List<Experience> experiences = input.getRequestEnvelope().getContext().getViewport().getExperiences();
		for (Experience experience : experiences) {
			strBuf.append(experience.toString() + "\n");
		}
		// TODO: uncomment logging
		// logger.info(strBuf.toString());
	}

	/**
	 * Helper method to create the image object for a display interface
	 * 
	 * @param imageUrl
	 *            The url of the image
	 * @return Image that is used in a body Template
	 */
	public static Image getImage(String imageUrl) {
		List<ImageInstance> sources = getImageInstance(imageUrl);
		return Image.builder().withSources(sources).build();
	}

	/**
	 * Helper method to create list of images
	 * 
	 * @param imageUrl
	 *            The uril of the image
	 * @return instances that is used in the image object
	 */
	public static List<ImageInstance> getImageInstance(String imageUrl) {
		List<ImageInstance> instances = new ArrayList<>();
		ImageInstance instance = ImageInstance.builder().withUrl(imageUrl).build();
		instances.add(instance);
		return instances;
	}

	/**
	 * Helper method that returns text content to be used in the display template.
	 * 
	 * @param primaryText
	 * @param secondaryText
	 * @return
	 */
	@Deprecated
	public static TextContent getTextContent(String primaryText, String secondaryText) {
		return TextContent.builder().withPrimaryText(makeRichText(primaryText))
				.withSecondaryText(makeRichText(secondaryText)).build();
	}

	/**
	 * Helper method that returns the rich text that can be set as text content for
	 * a display template.
	 * 
	 * @param text
	 *            The string that needs to be set as the text content for the body
	 *            template.
	 * @return RichText that will be rendered with the body template.
	 */
	@Deprecated
	public static RichText makeRichText(String text) {
		return RichText.builder().withText(text).build();
	}

	/**
	 * Helper method that returns the rich text that can be set as text content for
	 * a display template
	 * 
	 * @param text
	 * @return
	 */
	@Deprecated
	public static PlainText makePlainText(String text) {
		return PlainText.builder().withText(text).build();
	}

	// Image not required
	public static Template getBodyTemplate1(String title, String primaryText, String secondaryText, String tertiaryText,
			String backImgUrl) {
		Optional<String> optPri = Optional.ofNullable(primaryText);
		Optional<String> optSec = Optional.ofNullable(secondaryText);
		Optional<String> optTer = Optional.ofNullable(tertiaryText);
		Optional<String> optBack = Optional.ofNullable(backImgUrl);
		Image background = getImage(optBack.orElse(null));
		TextContent textContent = TextContentHelper.forRichText().withPrimaryText(optPri.get())
				.withSecondaryText(optSec.get()).withTertiaryText(optTer.get()).build();
		return BodyTemplate1.builder().withTitle(title).withTextContent(textContent).withBackgroundImage(background)
				.withToken(title).withBackButton(BackButtonBehavior.HIDDEN).build();
	}
	
	// Image not required
	public static Template getBodyTemplate1(String title, String primaryText, String secondaryText, String tertiaryText) {
		Optional<String> optPri = Optional.ofNullable(primaryText);
		Optional<String> optSec = Optional.ofNullable(secondaryText);
		Optional<String> optTer = Optional.ofNullable(tertiaryText);
		TextContent textContent = TextContentHelper.forRichText().withPrimaryText(optPri.get())
				.withSecondaryText(optSec.get()).withTertiaryText(optTer.get()).build();
		return BodyTemplate1.builder().withTitle(title).withTextContent(textContent)
				.withToken(title).withBackButton(BackButtonBehavior.HIDDEN).build();
	}

	// Image Required
	public static Template getBodyTemplate2(String title, String primaryText, String secondaryText, String tertiaryText,
			String backImgUrl, String imgUrl) {
		Optional<String> optPri = Optional.ofNullable(primaryText);
		Optional<String> optSec = Optional.ofNullable(secondaryText);
		Optional<String> optTer = Optional.ofNullable(tertiaryText);
		Optional<String> optBack = Optional.ofNullable(backImgUrl);
		Image background = getImage(optBack.orElse(imgUrl));
		Image templateImg= getImage(imgUrl);
		TextContent textContent = TextContentHelper.forRichText().withPrimaryText(optPri.get())
				.withSecondaryText(optSec.get()).withTertiaryText(optTer.get()).build();
		return BodyTemplate2.builder().withTitle(title).withTextContent(textContent).withImage(templateImg).withBackgroundImage(background)
				.withToken(title).withBackButton(BackButtonBehavior.HIDDEN).build();
	}

	// Image required
	public static Template getBodyTemplate3(String title, String primaryText, String secondaryText, String tertiaryText,
			String backImgUrl, String imgUrl) {
		Optional<String> optPri = Optional.ofNullable(primaryText);
		Optional<String> optSec = Optional.ofNullable(secondaryText);
		Optional<String> optTer = Optional.ofNullable(tertiaryText);
		Optional<String> optBack = Optional.ofNullable(backImgUrl);
		Image background = getImage(optBack.orElse(imgUrl));
		Image templateImg = getImage(imgUrl);
		TextContent textContent = TextContentHelper.forRichText().withPrimaryText(optPri.get())
				.withSecondaryText(optSec.get()).withTertiaryText(optTer.get()).build();
		return BodyTemplate3.builder().withTitle(title).withTextContent(textContent).withImage(templateImg).withBackgroundImage(background)
				.withToken(title).withBackButton(BackButtonBehavior.HIDDEN).build();
	}

	// Image not required
	/**
	 * Helper method to create body template 6
	 * 
	 * @param primaryText
	 *            The primary text to be displayed in the template on the show
	 * @param secondaryText
	 *            The secondary text to be displayed in the template on the show
	 * @param image
	 *            The url of the image
	 * @return template
	 */
	public static Template getBodyTemplate6(String title, String primaryText, String secondaryText, String backImgUrl) {
		Optional<String> optPri = Optional.ofNullable(primaryText);
		Optional<String> optSec = Optional.ofNullable(secondaryText);
		Optional<String> optBack = Optional.ofNullable(backImgUrl);
		Image background = getImage(optBack.orElse(null));
		String primary, secondary;
		primary = optPri.orElse("Primary Text");
		secondary = optSec.orElse("Secondary Text");
		return BodyTemplate6.builder().withTextContent(getTextContent(primary, secondary)).withBackgroundImage(background).withToken(title).withBackButton(BackButtonBehavior.HIDDEN).build();
	}
	
	public static Template getBodyTemplate6(String title, String primaryText, String secondaryText) {
		Optional<String> optPri = Optional.ofNullable(primaryText);
		Optional<String> optSec = Optional.ofNullable(secondaryText);
		String primary, secondary;
		primary = optPri.orElse("Primary Text");
		secondary = optSec.orElse("Secondary Text");
		return BodyTemplate6.builder().withTextContent(getTextContent(primary, secondary)).withToken(title).withBackButton(BackButtonBehavior.HIDDEN).build();
	}

	public static Template getBodyTemplate7(String title, String primaryText, String secondaryText,String backImgUrl, String imgUrl) {
		Optional<String> optPri = Optional.ofNullable(primaryText);
		Optional<String> optSec = Optional.ofNullable(secondaryText);
		Optional<String> optBack = Optional.ofNullable(backImgUrl);
		Image background = getImage(optBack.orElse(imgUrl));
		Image templateImg = getImage(imgUrl);
		String primary, secondary;
		primary = optPri.orElse("Primary Text");
		secondary = optSec.orElse("Secondary Text");
		return BodyTemplate6.builder().withTextContent(getTextContent(primary, secondary)).withImage(templateImg).withBackgroundImage(background).withToken(title).withBackButton(BackButtonBehavior.HIDDEN).build();
	}

	// List Template Helpers
	public static Template getListTemplate1(List<ListItem> listItems, String title) {
		return ListTemplate1.builder().withListItems(listItems).withTitle(title).withToken(title)
				.withBackButton(BackButtonBehavior.VISIBLE).build();
	}

	public static Template getListTemplate2(List<ListItem> listItems, String title) {
		return ListTemplate2.builder().withListItems(listItems).withTitle(title).withToken(title)
				.withBackButton(BackButtonBehavior.VISIBLE).build();
	}

	// Generic Display Response
	/**
	 * Helper method to create a response envelope for devices using displays.
	 * 
	 * @param speechText
	 *            Speech for Alexa
	 * @param cardTitle
	 *            Title for simple card
	 * @param cardText
	 *            Text for simple card
	 * @param template
	 *            Display template
	 * @return
	 */
	public static Optional<Response> displayResponse(String speechText, String cardTitle, String cardText,
			Template template) {
		return new ResponseBuilder().withSpeech(speechText).addRenderTemplateDirective(template).build();
	}

	/**
	 * Helper method to create a response envelope for device using displays
	 * 
	 * @param speechText
	 *            speech for Alexa
	 * @param card
	 *            card for response
	 * @param template
	 *            display template
	 * @return
	 */
	public static Optional<Response> displayResponse(String speechText, Card card, Template template) {
		return new ResponseBuilder().withSpeech(speechText).addRenderTemplateDirective(template).withCard(card).build();
	}

	/**
	 * Helper method to create list items.
	 * 
	 * @param name
	 * @param primaryText
	 * @param secondaryText
	 * @param tertiaryText
	 * @param imgUrl
	 * @return
	 */
	public static ListItem createListItem(String name,String primaryText, String secondaryText, String tertiaryText, String imgUrl) {
		Optional<String> optImg = Optional.ofNullable(imgUrl);
		Image itemImage = getImage(optImg.orElse(null));
		TextContent textContent=TextContentHelper.forPlainText().withPrimaryText(primaryText).withSecondaryText(secondaryText).withTertiaryText(tertiaryText).build();
		return ListItem.builder().withTextContent(textContent).withImage(itemImage).withToken(name).build();
	}
	
	/**
	 * Helper method to create list items with out an image with Plain Text
	 * 
	 * @param name
	 * @param primaryText
	 * @param secondaryText
	 * @param tertiaryText
	 * @return
	 */
	public static ListItem createListItemNoImage(String name,String primaryText, String secondaryText, String tertiaryText) {
		return createListItemNoImage(name, primaryText, secondaryText, tertiaryText, false);
	}

	/**
	 * Helper method to create list items with out an image with an option to choose between Richt Text and Plain Text
	 * 
	 * @param name
	 * @param primaryText
	 * @param secondaryText
	 * @param tertiaryText
	 * @param isRichText
	 * @return
	 */
	public static ListItem createListItemNoImage(String name,String primaryText, String secondaryText, String tertiaryText, boolean isRichText) {
		TextContent textContent = null;
		if(isRichText) {
			textContent=TextContentHelper.forRichText().withPrimaryText(primaryText).withSecondaryText(secondaryText).withTertiaryText(tertiaryText).build();
		} else {
			textContent=TextContentHelper.forPlainText().withPrimaryText(primaryText).withSecondaryText(secondaryText).withTertiaryText(tertiaryText).build();
		}
		return ListItem.builder().withTextContent(textContent).withToken(name).build();
	}
	
}
