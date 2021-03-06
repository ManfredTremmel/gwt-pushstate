package com.wallissoftware.pushstate.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Hyperlink;
import com.wallissoftware.pushstate.client.PushStateHistorian;

/**
 * Common logic for {@link HyperlinkPushState} and {@link InlineHyperlinkPushState}.
 *
 * @author Daniel Hari
 *
 */
class HyperlinkPushStateDelegate {

  private static final Historian HISTORIAN = GWT.create(Historian.class);

  private String targetHistoryToken;

  private final Hyperlink target;

  HyperlinkPushStateDelegate(final Hyperlink hyperlink) {
    this.target = hyperlink;
  }

  public void setTargetHistoryToken(final String ptargetHistoryToken) {
    assert ptargetHistoryToken != null : "New history item cannot be null!";

    this.targetHistoryToken = CodeServerParameterHelper.append(ptargetHistoryToken);

    final String href =
        PushStateHistorian.getRelativePath() + this.ensureHasRoot(this.targetHistoryToken);
    this.target.getElement().setPropertyString("href", href);
  }

  private String ensureHasRoot(final String ptargetHistoryToken) {
    return (ptargetHistoryToken.length() > 0 && ptargetHistoryToken.charAt(0) == '/')
        ? ptargetHistoryToken : "/" + ptargetHistoryToken;
  }

  public String getTargetHistoryToken() {
    return this.targetHistoryToken;
  }

  public void onBrowserEvent(final Event pevent) {
    switch (DOM.eventGetType(pevent)) {
      case Event.ONMOUSEOVER:
      case Event.ONMOUSEOUT:
        // Only fire the mouse over event if it's coming from outside this widget.
        // Only fire the mouse out event if it's leaving this widget.
        final Element related = pevent.getRelatedEventTarget().cast();
        if (related != null && this.target.getElement().isOrHasChild(related)) {
          return;
        }
        break;
      default:
        break;
    }
    DomEvent.fireNativeEvent(pevent, this.target, this.target.getElement());
    if (DOM.eventGetType(pevent) == Event.ONCLICK && !pevent.getCtrlKey()) {
      HyperlinkPushStateDelegate.HISTORIAN.newItem(this.getTargetHistoryToken(), true);
      pevent.preventDefault();
    }
  }
}
