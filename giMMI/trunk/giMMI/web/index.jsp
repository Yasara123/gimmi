<%@ page import="gimmi.content.*"%>
<%@ page import="gimmi.database.*"%>
<%@ page import="java.sql.ResultSet"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>giMMI - Einzelanalyse</title>
<link type="text/css" href="css/smoothness/jquery-ui-1.8.16.custom.css"
    rel="stylesheet" />
<link type="text/css" href="css/screen.css" rel="stylesheet" />
<link type="text/css" href="css/chosen.css" rel="stylesheet" />
<!-- <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script> -->
<script
    src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"
    type="text/javascript"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/chosen.jquery.js"></script>
<script type="text/javascript" src="js/jquery.hotkeys.js"></script>
<script type="text/javascript">
	var tab_counter = 1;
	var tabs;

	//onLoad
	$(function() {
		// Tabs
		//$('#tabs').tabs();

		$('#tabs-1').html(getNewTabContent());
		newTabSubmitOnClick(1);

		// tabs init with a custom tab template and an "add" callback filling in the content
		$tabs = $("#tabs")
				.tabs(
						{
							tabTemplate : '<li><a href="#{href}">#{label}</a> <span class="ui-icon ui-icon-close" title="Tab schlie�en">Remove Tab</span></li>',
							add : function(event, ui) {
								$(ui.panel).append(
										getNewTabContent(tab_counter));
								newTabSubmitOnClick(tab_counter);
								$tabs.tabs('select', '#' + ui.panel.id);
							}
						});

		//add closing functionality to tabs
		$('#tabs span.ui-icon-close').live('click', function() {
			var index = $('li', $tabs).index($(this).parent()) - 2; //-2 because of the preceding list elements
			$tabs.tabs('remove', index);
		});

		//make tabs renameable
		$('#tabs li a')
				.live(
						'dblclick',
						function() {
							var new_name = prompt(
									"Geben Sie den neuen Namen f�r den Tab ein",
									"");
							if (new_name) {
								new_name = (new_name == "") ? $(this).html()
										: new_name;
								$(this).html(new_name);
							}
						});

		//add on click functionality to "new tab" symbol
		$('#add_tab').live('click', addTab).live('mouseover', function() {
			$(this).css('cursor', 'pointer');
		});

		//make tabs sortable
		$tabs.find(".ui-tabs-nav").sortable({
			axis : "x"
		});

		//add datepicker
		//$.datepicker.setDefaults( $.datepicker.regional[ "de" ] );
		//$("#crawl-earliest").datepicker();
		//$("#crawl-latest").datepicker();

		$('#prev_tab').click(prevTab).bind('mouseover', function() {
			$(this).css('cursor', 'pointer');
		});
		$('#next_tab').click(nextTab).bind('mouseover', function() {
			$(this).css('cursor', 'pointer');
		});

		//initialize hotkeys to switch tabs using the keyboard
		$(document).bind('keydown', 'alt+ctrl+left', prevTab);
		$(document).bind('keydown', 'alt+ctrl+right', nextTab);
		$(document).bind('keydown', 'alt+w', closeSelectedTab);

		//hin und herschalten zwischen domains hinzuf�gen
		$('#prev_page').live('click', function() {
			switchDomain('prev');
		}).live('mouseover', function() {
			$(this).css('cursor', 'pointer');
		});
		$('#next_page').live('click', function() {
			switchDomain('next');
		}).live('mouseover', function() {
			$(this).css('cursor', 'pointer');
		});

		$('.show-additional-information').live('mouseover', function() {
			$(this).css('cursor', 'pointer');
		}).live(
				'click',
				function() {
					var additional_info = $(this).next();
					var display = additional_info.css('display');
					var spans = $('span', this);
					if (display == 'none') {
						additional_info.show(100);
						spans.eq(0).removeClass('ui-icon-circle-plus')
								.addClass('ui-icon-circle-minus');
						spans.eq(1).html('Weniger Informationen zeigen');
					} else {
						additional_info.hide(100);
						spans.eq(0).removeClass('ui-icon-circle-minus')
								.addClass('ui-icon-circle-plus');
						spans.eq(1).html('Mehr Informationen zeigen');
					}
				});

	});

	//select previous tab
	function prevTab() {
		var index = $tabs.tabs('option', 'selected');
		var new_index;
		if (index > 0) {
			new_index = index - 1;
		} else {
			new_index = ($tabs.tabs('length') - 1);
		}
		$tabs.tabs('select', new_index);
	}

	//select next tab
	function nextTab() {
		var index = $tabs.tabs('option', 'selected');
		var new_index;
		if (index < ($tabs.tabs('length') - 1)) {
			new_index = index + 1;
		} else {
			new_index = 0;
		}
		$tabs.tabs('select', new_index);
	}

	function closeSelectedTab() {
		var index = $tabs.tabs('option', 'selected');
		$tabs.tabs('remove', index);
	}

	//returns the id of the selected tab, either as a string or as an int (must be specified via return_type)
	//returns id as string by default
	function getCurrentTabId(return_type) {

		var selected = $tabs.tabs('option', 'selected');
		var tab_id = $('a', $('#tabs ul li').eq(selected + 2)).attr('href');

		if (return_type == 'string') {
			return tab_id;
		} else if (return_type == 'int') {
			return parseInt(tab_id.substring(6, tab_id.length));
		}
		return tab_id;
	}

	//add tab
	function addTab() {
		tab_counter++;
		var tab_id = tab_counter;
		var tab_title = "Neuer Tab";
		var add_li = $('#add_tab');
		$('#add_tab').remove();
		$tabs.tabs('add', '#tabs-' + tab_id, tab_title);
		$('#tab-list').append(add_li);
	}

	//returns the content for a new tab
	function getNewTabContent(tab_id) {
		return $('#new_tab').html().replace(/-id-/g, tab_counter);
	}

	//adds the on click functionality to the submit button of newly added tabs
	function newTabSubmitOnClick(tab_id) {
		$('#new_tab_form_submit_' + tab_id).click(function() {
			createSelection(tab_id);
			return false;
		});
	}

	function createSelection(tab_id) {
		var div = $('#tabs-' + tab_id);
		div.html($('#tab_template').html());
		div.append('<iframe src="http://www.example.com"></iframe>');
		//initialize chosen
		$('.chzn-select', div).chosen().change({
			no_results_text : "Keine Domains in Auswahl gefunden"
		}, function(e) {
			//selectPage($(getCurrentTabId('string') + ' .result-selected').html());
			//alert($(this).html());
			selectPage($(this).val());
		});
	}

	function selectPage(page_id) {
		var url = 'http://www.'
				+ $(getCurrentTabId('string') + ' .chzn-single span').html();
		changeiFrameUrl(url);
	}

	function switchDomain(dir) {
		var tab_id = getCurrentTabId('string');
		var current_index = $(tab_id + ' .result-selected').index();
		var list = $(tab_id + ' .chzn-results li');
		var size = list.size();
		var new_index = current_index;

		if (dir == 'next') {
			new_index = (current_index < (size - 1)) ? current_index + 1 : 0;
		} else if (dir == 'prev') {
			new_index = (current_index > 0) ? current_index - 1 : (size - 1);
		}

		//alert('tab_id: ' + tab_id + ' -- size: ' + size + ' -- current: ' + current_index + ' -- new: ' + new_index);
		if (new_index != current_index) {
			var old_item = list.eq(current_index);
			var new_item = list.eq(new_index);

			var url = new_item.html();
			changeiFrameUrl('http://www.' + url);

			old_item.removeClass('result-selected');
			new_item.addClass('result-selected');

			$(tab_id + ' .chzn-single span').html(url);
		}
	}

	function changeiFrameUrl(url) {
		var tab_id = getCurrentTabId('string');
		$('#tabs ' + tab_id + ' iframe').attr('src', url);
	}
</script>
</head>
<!-- jQuery UI dependencies:
    - core
    - widget
    - tabs
    - mouse
    - sortable
    - datepicker ??
-->

<body>
    <div id="header">
        <div id="header-text">
            <h1>giMMI</h1>
        </div>
        <div id="header-links">
            <ul>
                <li class="active"><a href="#">Einzelanalyse</a></li>
                <li><a href="#">Suche</a></li>
                <li><a href="#">Statistiken</a></li>
                <li><a href="#">Hilfe</a></li>
            </ul>
        </div>
    </div>

    <div id="tabs">

        <ul id="tab-list">
            <li id="prev_tab"><span
                class="ui-icon ui-icon-triangle-1-w"
                title="Vorherigen Tab ausw�hlen (Strg + Alt + Pfeiltaste links)">Previous
                    Tab</span></li>
            <li id="next_tab"><span
                class="ui-icon ui-icon-triangle-1-e"
                title="N�chsten Tab ausw�hlen (Strg + Alt + Pfeiltaste rechts)">Next
                    Tab</span></li>
            <li><a href="#tabs-1">Neuer Tab</a> <span
                class="ui-icon ui-icon-close" title="Tab schlie�en">Remove
                    Tab</span></li>
            <li id="add_tab"><span
                class="ui-icon ui-icon-circle-plus"
                title="Neuen Tab erstellen">Add Tab</span></li>
        </ul>

        <div id="tabs-1"></div>

    </div>

    <div id="new_tab" style="display: none;">
        <h2>Auswahlkriterien f�r zu analysierende Seiten</h2>
        <p>Kriterien k�nnen auch leer gelassen werden!</p>
        <form id="new_tab_form_-id-">
            <table style="border: none; width: 300px;">
                <tr>
                    <td style="font-weight: bold;"><label
                        for="language_-id-">Sprache</label></td>
                    <td><select name="language_-id-"
                        id="language_-id-" style="width: 150px;">
                            <option value="" selected="selected">--</option>
                            <%
                            	Language language = new Language(Database.getInstance());
                            	ResultSet languages = language.getAllEntries(true);
                            	while (languages.next()) {
                            %>
                            <%="<option value=" + languages.getInt("language_id") + ">"%>
                            <%=languages.getString("name_de")%>
                            <%="</option>"%>
                            <%
                            	}
                            %>
                    </select></td>
                </tr>
                <tr>
                    <td style="font-weight: bold;"><label
                        for="country_-id-">Land</label></td>
                    <td><select name="country_-id-"
                        id="country_-id-" style="width: 150px;">
                            <option value="" selected="selected">--</option>
                            <%
                            	Country country = new Country(Database.getInstance());
                            	ResultSet countries = country.getAllEntries(true);
                            	while (countries.next()) {
                            %>
                            <%="<option value=" + countries.getInt("country_id")+ ">"%>
                            <%=countries.getString("name_de")%>
                            <%="</option>"%>
                            <%
                            	}
                            %>
                    </select></td>
                </tr>
                <tr>
                    <td style="font-weight: bold;"><label
                        for="top-category_-id-">Kategorie</label></td>
                    <td><select name="top-category_-id-"
                        id="top-category_-id-" style="width: 150px;">
                            <option value="" selected="selected">--</option>
                            <%
                            	Category category = new Category(Database.getInstance());
                            	ResultSet categories = category.getAllEntries(true);
                            	while (categories.next()) {
                            %>
                            <%="<option value=" + categories.getInt("category_id")+ ">"%>
                            <%=categories.getString("name_de")%>
                            <%="</option>"%>
                            <%
                            	}
                            %>
                    </select></td>
                </tr>
                <tr>
                    <td style="font-weight: bold;">Crawldatum</td>
                    <td><label for="crawl-earliest_-id-">Von</label>
                        <input id="crawl-earliest_-id-" type="text"
                        name="crawl-earliest_-id-" size=10
                        value="dd.mm.yyyy"> <label
                        for="crawl-latest_-id-">Bis</label> <input
                        id="crawl-latest_-id-" type="text"
                        name="crawl-latest_-id-" size=10
                        value="dd.mm.yyyy"></td>
                </tr>
                <tr>
                    <td style="text-align: right; height: 40px; vertical-align: bottom;"
                        colspan=2>
                        <button id="new_tab_form_submit_-id-">
                            Auswahl erstellen
                        </button>
                    </td>
            </table>
        </form>
    </div>

    <div id="tab_template" style="display: none;">
        <div>
            <div style="float: left; padding-right: 10px;">
                <h2 style="margin: 0;">Einschr�nkungen</h2>
            </div>

            <div>
                <ul class="ul-horizontal">
                    <li><b>Sprache: </b> deutsch</li>
                    <li><b>Land: </b>-</li>
                    <li><b>Kategorie: </b>Sport</li>
                    <li><b>Crawlzeitraum: </b> 10.10.2010 -
                        10.10.2011</li>
                </ul>
                <br /> 
                <span><b>50 Seiten</b> zu dieser Auswahl
                    im Korpus</span>
            </div>
        </div>

        <div class="domain-container" style="padding: 0;">
            <div class="domain-switch">
                <span id="prev_page"
                    class="ui-icon ui-icon-triangle-1-w"
                    title="Vorhergehende Domain aus Auswahl anzeigen">Previous
                    Page</span> <span id="next_page"
                    class="ui-icon ui-icon-triangle-1-e"
                    title="N�chste Domain aus Auswahl anzeigen">Next
                    Page</span>
            </div>
            <div class="domain">
                <!-- <h2>www.example.com</h2> -->
                <select data-placeholder="Domain ausw�hlen..."
                    class="chzn-select" style="width: 350px;"
                    tabindex="4">
                    <option value="0" selected="selected">example.com</option>
                    <option value="1">w3.org</option>
                    <option value="2">linkedin.com</option>
                    <option value="3">reddit.com</option>
                    <option value="4">adobe.com</option>
                    <option value="5">bit.ly</option>
                    <option value="6">wordpress.com</option>
                    <option value="7">bbc.co.uk</option>
                    <option value="8">wikipedia.com</option>
                    <option value="9">yahoo.com</option>
                    <option value="10">flickr.com</option>
                </select>
            </div>
        </div>

        <ul class="ul-horizontal">
            <li><b>Sprache: </b>deutsch</li>
            <li><b>Land: </b>�sterreich</li>
            <li><b>Kategorie: </b>Sport - Fu�ball</li>
            <li><b>Crawldatum: </b>08.10.2011</li>
        </ul>

        <br /> <br />
        <div class="show-additional-information"
            style="clear: both; width: 200px;">
            <span class="ui-icon ui-icon-circle-plus"
                style="float: left;">Mehr Informationen zeigen</span> <span>Mehr
                Infos anzeigen</span>
        </div>
        <div class="additional-information" style="display: none;">
            <ul class="ul-horizontal">
                <li><b>Anzahl Links: </b>30</li>
                <li><b>Linkdichte: </b>0,34</li>
                <li><b>Anzahl Bilder: </b>17</li>
                <li><b>Bilddichte: </b>0,02</li>
                <li><b>Anzahl Links: </b>30</li>
                <li><b>Linkdichte: </b>0,34</li>
                <li><b>Anzahl Bilder: </b>17</li>
                <li><b>Bilddichte: </b>0,02</li>
                <li><b>Anzahl Links: </b>30</li>
                <li><b>Linkdichte: </b>0,34</li>
                <li><b>Anzahl Bilder: </b>17</li>
                <li><b>Bilddichte: </b>0,02</li>
                <li><b>Anzahl Links: </b>30</li>
                <li><b>Linkdichte: </b>0,34</li>
                <li><b>Anzahl Bilder: </b>17</li>
                <li><b>Bilddichte: </b>0,02</li>
            </ul>
        </div>

    </div>
</body>
</html>