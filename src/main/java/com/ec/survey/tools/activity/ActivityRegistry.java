package com.ec.survey.tools.activity;

import org.apache.commons.lang3.Range;

import java.util.*;

public class ActivityRegistry {


    public static final String
            OBJ_SURVEY = "Survey",
            OBJ_DRAFT_SURVEY = "DraftSurvey",
            OBJ_SURVEY_AND_DRAFT = "SurveyAndDraft",
            OBJ_ACTIVITIES = "Activities",
            OBJ_RESULTS = "Results",
            OBJ_CONTRIBUTION = "Contribution",
            OBJ_TEST_CONTRIBUTION = "TestContribution",
            OBJ_GUEST_LIST = "GuestList",
            OBJ_PRIVILEGES = "Privileges",
            OBJ_MESSAGES = "Messages",
            OBJ_COMMENT = "Comment";

    public static final String
            PROP_NA = "n/a",
            PROP_STATE = "State",
            PROP_PENDING_CHANGES = "PendingChanges",
            PROP_ALIAS = "Alias",
            PROP_END_NOTIFICATION_STATE = "EndNotificationState",
            PROP_END_NOTIFICATION_VALUE = "EndNotificationValue",
            PROP_END_NOTIFICATION_REACH = "EndNotificationReach",
            PROP_CONTACT_CREATION = "ContactCreation",
            PROP_SECURITY = "Security",
            PROP_PASSWORD = "Password",
            PROP_ANONYMITY = "Anonymity",
            PROP_PRIVACY = "Privacy",
            PROP_CAPTCHA = "Captcha",
            PROP_EDIT_CONTRIBUTION = "EditContribution",
            PROP_MULTI_PAGING = "MultiPaging",
            PROP_PAGE_WISE_VALIDATION = "PageWiseValidation",
            PROP_WCAG_COMPLIANCE = "WCAGCompliance",
            PROP_OWNER = "Owner",
            PROP_PROGRESS_BAR = "ProgressBar",
            PROP_MOTIVATION_POP_UP = "MotivationPopUp",
            PROP_PROPERTIES = "Properties",
            PROP_USEFUL_LINK = "UsefulLink",
            PROP_BACKGROUND_DOCUMENT = "BackgroundDocument",
            PROP_TITLE = "Title",
            PROP_PIVOT_LANGUAGE = "PivotLanguage",
            PROP_CONTACT = "Contact",
            PROP_AUTOPUBLISH = "Autopublish",
            PROP_START_DATE = "StartDate",
            PROP_END_DATE = "EndDate",
            PROP_LOGO = "Logo",
            PROP_SKIN = "Skin",
            PROP_AUTO_NUMBERING_SECTIONS = "AutoNumberingSections",
            PROP_AUTO_NUMBERING_QUESTIONS = "AutoNumberingQuestions",
            PROP_ELEMENT_ORDER = "ElementOrder",
            PROP_SURVEY_ELEMENT = "SurveyElement",
            PROP_TRANSLATION = "Translation",
            PROP_CONFIRMATION_PAGE = "ConfirmationPage",
            PROP_ESCAPE_PAGE = "EscapePage",
            PROP_PUBLISH_INDIVIDUAL = "PublishIndividual",
            PROP_PUBLISH_CHARTS = "PublishCharts",
            PROP_PUBLISH_STATISTICS = "PublishStatistics",
            PROP_PUBLIC_SEARCH = "PublicSearch",
            PROP_PUBLISH_QUESTION_SELECTION = "PublishQuestionSelection",
            PROP_PUBLISH_ANSWER_SELECTION = "PublishAnswerSelection",
            PROP_EXPORT_STATISTICS = "ExportStatistics",
            PROP_EXPORT_CONTENT = "ExportContent",
            PROP_EXPORT_CHARTS = "ExportCharts",
            PROP_EXPORT_ACTIVITIES = "ExportActivities",
            PROP_PUBLISH_UPLOADED_ELEMENTS = "PublishUploadedElements",
            PROP_EXPORT_UPLOADED_ELEMENTS = "ExportUploadedElements",
            PROP_DELETE_COLUMN = "DeleteColumn",
            PROP_EXPORT = "Export",
            PROP_TOKEN_CONTACTS_DEPARTMENT = "Token/Contacts/Department/VoterFile",
            PROP_INVITATIONS = "Invitations",
            PROP_END_NOTIFICATION_MESSAGE = "EndNotificationMessage",
            PROP_QUORUM = "Quorum",
            PROP_LIST_PORTION = "EligibleLists",
            PROP_NUM_PREFERENTIAL_VOTES = "MaximumPreferentialVotes",
            PROP_SEATS = "NumberOfSeatsToAllocate",
            PROP_RESULT_TEST_PAGE = "EnableResultsTestPage";

    public static final String 
            EVT_ADDED = "Added",
            EVT_APPLIED = "Applied",
            EVT_MODIFIED = "Modified",
            EVT_DISCARDED = "Discarded",
            EVT_DELETED = "Deleted", 
            EVT_SAVED = "Saved", 
            EVT_REMOVED = "Removed", 
            EVT_ENABLED = "Enabled", 
            EVT_DISABLED = "Disabled", 
            EVT_REQUESTED = "Requested", 
            EVT_RETURNED = "Returned", 
            EVT_STARTED = "Started", 
            EVT_CREATED = "Created", 
            EVT_PAUSED = "Paused", 
            EVT_SENT = "Sent",
            EVT_OPENED = "Opened",
            EVT_SUBMITTED = "Submitted",
            EVT_ACCEPTED = "Accepted",
            EVT_REJECTED = "Rejected",
            EVT_EXPIRED = "Expired";

    /**
     * Add new activity ids here
     */
    public static final int
            ID_SURVEY_CREATED = 101, // Survey successfully created
            ID_SURVEY_IMPORTED = 102, // Survey successfully imported
            ID_SURVEY_COPIED = 103, // Survey successfully copied
            ID_SURVEY_DELETED = 104, // Survey deleted
            ID_SURVEY_STATE_MANUAL = 105, // Survey state changed manually
            ID_SURVEY_STATE_AUTO = 106, // Survey state changed automatically
            ID_PENDING_APPLIED = 107, // Pending changes applied
            ID_PENDING_DISCARDED = 108, // Pending changes cleared
            ID_ALIAS = 109, // Alias changed
            ID_END_NOTIFICATION_SETTINGS = 110, // End notification settings modified
            ID_END_NOTIFICATION_VALUES = 111, // Values for sending end notification modified
            ID_END_NOTIFICATION_REACH = 112, // Reach for end notifications modified
            ID_CONTACT_CREATION = 113, // Contact creation settings modified
            ID_SECURITY_SETTINGS = 114, // Security settings modified
            ID_GLOBAL_PASSWORD = 115, // Global password modified
            ID_ANONYMITY_SETTINGS = 116, // Anonymity settings modified
            ID_PRIVACY_SETTINGS = 117, // Privacy settings modified
            ID_CAPTCHA_SETTINGS = 118, // Captcha settings modified
            ID_EDIT_CONTRIBUTION = 119, // Edit contribution settings modified
            ID_MULTI_PAGING_SETTINGS = 120, // Multi-paging settings modified
            ID_PAGEWISE_VALIDATION = 121, // Pagewise validation settings modified
            ID_WCAG_COMPLIANCE = 122, // WCAG Compliance settings modified
            ID_OWNER = 123, // Owner changed
            ID_PROGRESS_BAR = 124, // Progressbar changed
            ID_MOTIVATION_POPUP = 128, // Motivation Popup changed
            ID_EVOTE_QUORUM = 129, //Quorum changed
            ID_EVOTE_LIST_PORTION = 130, //Minimum portion of votes for lists changed
            ID_EVOTE_NUM_PREFERENTIAL_VOTES = 131, //Maximum number of votes changed
            ID_EVOTE_SEATS = 132, //Number of seats changed
            ID_EVOTE_TEST_PAGE = 133, //Evote Test Result Page changed
            ID_CHANGE_OWNER_REQUEST_SENT = 134, //A new request to change Survey Ownership was sent
            ID_CHANGE_OWNER_REQUEST_ACCEPTED = 135, //The request to change Survey Ownership was accepted
            ID_CHANGE_OWNER_REQUEST_REJECTED = 136, //The request to change Survey Ownership was rejected
            ID_CHANGE_OWNER_REQUEST_EXPIRED = 137, //The request to change Survey Ownership has expired

            ID_SURVEY_LOADED = 201, // Survey successfully loaded
            ID_PROPERTIES = 202, // Properties updated
            ID_USEFUL_LINK_ADD = 203, // Useful link added
            ID_USEFUL_LINK_REMOVE = 204, // Useful link removed
            ID_BACKGROUND_DOC_ADD = 205, // Background document added
            ID_BACKGROUND_DOC_REMOVE = 206, // Background document removed
            ID_SURVEY_TITLE = 207, // Survey title modified
            ID_MAIN_LANGUAGE = 208, // Main language modified
            ID_CONTACT_INFO = 209, // Contact Information modified
            ID_AUTO_SUBMIT = 210, // Autosubmission settings modified
            ID_START_DATE = 211, // Start-date modified
            ID_END_DATE = 212, // End-date modified
            ID_LOGO = 213, // Logo modified
            ID_SKIN = 214, // Skin modified
            ID_AUTO_NUM_SECTIONS = 215, // Autonumbering modified for sections
            ID_AUTO_NUM_QUESTIONS = 216, // Autonumbering modified for questions
            ID_ELEMENT_ORDER = 217, // Order of Survey Elements modified (Save Editor)
            ID_ELEMENT_ADDED = 218, // Survey element added
            ID_ELEMENT_REMOVED = 219, // Survey element removed
            ID_ELEMENT_UPDATED = 220, // Survey element updated
            ID_TRANSLATION_ADDED = 221, // New translation added
            ID_TRANSLATION_REMOVED = 222, // Translation deleted
            ID_TRANSLATION_ENABLED = 223, // Existing translation enabled
            ID_TRANSLATION_DISABLED = 224, // Existing translation disabled
            ID_CONFIRM_PAGE = 225, // Confirmation page modified
            ID_ESCAPE_PAGE = 226, // Escape page modified
            ID_TRANSLATION_MODIFIED = 227, // Translation modified
            ID_MACHINE_TRANSLATION = 228, // Machine translation requested

            ID_CONTENT_PUBLICATION = 301, // Content publication modified
            ID_CHARTS_PUBLICATION = 302, // Charts publication modified
            ID_STATISTICS_PUBLICATION = 303, // Statistics publication modified
            ID_PUBLIC_RESULTS_SEARCH = 304, // Search on public results modified
            ID_PUBLISH_QUESTION_SET = 305, // Question-set set to be published modified
            ID_PUBLISH_ANSWER_SET = 306, // Answers-set to be published modified
            ID_STATISTICS_EXPORT = 307, // Start an export of statistical results
            ID_CONTENT_EXPORT = 308, // Content export started
            ID_CHART_EXPORT = 309, // Chart export started
            ID_EXPORT_FINISHED = 310, // Export task returned
            ID_EXPORT_DELETED = 311, // Existing export task deleted
            ID_ACTIVITY_EXPORT = 312, // Activity export started
            ID_UPLOADED_ELEMENTS_PUBLISH = 313, // Upload elements publication modified
            ID_UPLOADED_ELEMENTS_DOWNLOAD = 314, // Upload elements are downloaded from results
            ID_BLANK_ANSWERS = 315, // Blank answers from question in results
            ID_OPEN_QUORUM = 316, //Quorum page was accessed
            ID_DISPLAY_RESULTS = 317, //'Display Results' button on eVote results page was clicked
            ID_ALLOCATE_SEATS = 318, //'Allocate Seats' button on eVote results page was clicked
            ID_EXPORT_SEATS = 319, //'Export' button on eVote results page was clicked
            ID_RESULTS_ACCESS = 320, //Results page was accessed

            ID_CONTRIBUTION_SUBMIT = 401, // Contribution has been submitted
            ID_CONTRIBUTION_DELETE = 402, // Contribution has been deleted
            ID_CONTRIBUTION_EDIT = 403, // Contribution has been modified
            ID_TEST_SUBMIT = 404, // Test contribution has been submitted
            ID_TEST_DELETE = 405, // Test contribution has been deleted
            ID_TEST_EDIT = 406, // Test contribution has been modified
            ID_DRAFT_CONTRIBUTION_SUBMIT = 407, // Draft Contribution has been submitted

            ID_GUEST_LIST_CREATED = 501, // Guestlist created
            ID_GUEST_LIST_REMOVED = 502, // Guestlist removed
            ID_GUEST_LIST_PAUSED = 503, // Guestlist paused
            ID_GUEST_LIST_STARTED = 504, // Guestlist started
            ID_GUEST_LIST_MODIFIED = 505, // Guestlist modified
            ID_GUEST_LIST_EMAIL = 506, // E-mail sending started
            ID_GUEST_LIST_CONTACT_CHANGED = 507, // Contact has been changed in guestlist.
            ID_GUEST_LIST_VOTER_ADDED = 508, // A voter was added to the guestlist
            ID_GUEST_LIST_VOTER_REMOVED = 509, // A voter was removed from the guestlist

            ID_DELEGATE_MANAGER_ADD = 601, // Delegate manager added
            ID_PRIVILEGES_EDIT = 602, // Existing privilege settings modified
            ID_PRIVILEGES_DELETE = 603, // Existing privilege setting deleted

            ID_END_NOTIFICATION_SENT = 701, // End notification message sent

            ID_COMMENT_EDITED = 801, // Comment modified by form manager
            ID_COMMENT_DELETED = 802; // Comment deleted by form manager

    private final static LinkedHashMap<String, Range<Integer>> objectsMap; //Linked Map because the order is important for OBJ_ACTIVITIES and OBJ_SURVEY_AND_DRAFT

    private final static Map<Integer, ActivityRegistryEntry> idToEntryMap;
    private final static Map<String, List<Integer>> propertyToIdsMap;
    private final static Map<String, List<Integer>> eventToIdsMap;

    private final static Integer[] sortedActivityIds;


    static {
        objectsMap = new LinkedHashMap<>();

        registerObjects();

        idToEntryMap = new HashMap<>();
        propertyToIdsMap = new HashMap<>();
        eventToIdsMap = new HashMap<>();

        registerIds();

        sortedActivityIds = idToEntryMap.keySet().toArray(new Integer[0]);
        Arrays.sort(sortedActivityIds);
    }

    private static void registerObjects(){
        registerObject(OBJ_SURVEY, 100, 199);
        registerObject(OBJ_DRAFT_SURVEY, 200, 299);
        registerObject(OBJ_SURVEY_AND_DRAFT, 100, 299);
        registerObject(OBJ_ACTIVITIES, 312, 312);
        registerObject(OBJ_RESULTS, 300, 399);
        registerObject(OBJ_CONTRIBUTION, 400, 403);
        registerObject(OBJ_TEST_CONTRIBUTION, 405, 499);
        registerObject(OBJ_GUEST_LIST, 500, 599);
        registerObject(OBJ_PRIVILEGES, 600, 699);
        registerObject(OBJ_MESSAGES, 700, 799);
        registerObject(OBJ_COMMENT, 800, 899);
    }

    /**
     * Register new Activities here
     * Also add a new ensureActivity(<id/>, session); call in the SchemaService
     * Make sure that you add a 'logging.<id/> = <text/>' entry in messages_en
     */
    private static void registerIds(){
        
        /* Auto Generated from previous sources */

        //Survey 100
        register(PROP_NA, EVT_DELETED, ID_SURVEY_DELETED);
        register(PROP_NA, EVT_CREATED, ID_SURVEY_CREATED, ID_SURVEY_IMPORTED, ID_SURVEY_COPIED);
        register(PROP_STATE, EVT_MODIFIED, ID_SURVEY_STATE_MANUAL, ID_SURVEY_STATE_AUTO);
        register(PROP_PENDING_CHANGES, EVT_APPLIED, ID_PENDING_APPLIED);
        register(PROP_PENDING_CHANGES, EVT_DISCARDED, ID_PENDING_DISCARDED);
        register(PROP_ALIAS, EVT_MODIFIED, ID_ALIAS);
        register(PROP_END_NOTIFICATION_STATE, EVT_MODIFIED, ID_END_NOTIFICATION_SETTINGS);
        register(PROP_END_NOTIFICATION_VALUE, EVT_MODIFIED, ID_END_NOTIFICATION_VALUES);
        register(PROP_END_NOTIFICATION_REACH, EVT_MODIFIED, ID_END_NOTIFICATION_REACH);
        register(PROP_CONTACT_CREATION, EVT_MODIFIED, ID_CONTACT_CREATION);
        register(PROP_SECURITY, EVT_MODIFIED, ID_SECURITY_SETTINGS);
        register(PROP_PASSWORD, EVT_MODIFIED, ID_GLOBAL_PASSWORD);
        register(PROP_ANONYMITY, EVT_MODIFIED, ID_ANONYMITY_SETTINGS);
        register(PROP_PRIVACY, EVT_MODIFIED, ID_PRIVACY_SETTINGS);
        register(PROP_CAPTCHA, EVT_MODIFIED, ID_CAPTCHA_SETTINGS);
        register(PROP_EDIT_CONTRIBUTION, EVT_MODIFIED, ID_EDIT_CONTRIBUTION);
        register(PROP_MULTI_PAGING, EVT_MODIFIED, ID_MULTI_PAGING_SETTINGS);
        register(PROP_PAGE_WISE_VALIDATION, EVT_MODIFIED, ID_PAGEWISE_VALIDATION);
        register(PROP_WCAG_COMPLIANCE, EVT_MODIFIED, ID_WCAG_COMPLIANCE);
        register(PROP_OWNER, EVT_MODIFIED, ID_OWNER);
        register(PROP_PROGRESS_BAR, EVT_MODIFIED, ID_PROGRESS_BAR);
        register(PROP_MOTIVATION_POP_UP, EVT_MODIFIED, ID_MOTIVATION_POPUP);

        register(PROP_QUORUM, EVT_MODIFIED, ID_EVOTE_QUORUM);
        register(PROP_LIST_PORTION, EVT_MODIFIED, ID_EVOTE_LIST_PORTION);
        register(PROP_NUM_PREFERENTIAL_VOTES, EVT_MODIFIED, ID_EVOTE_NUM_PREFERENTIAL_VOTES);
        register(PROP_SEATS, EVT_MODIFIED, ID_EVOTE_SEATS);
        register(PROP_RESULT_TEST_PAGE, EVT_MODIFIED, ID_EVOTE_TEST_PAGE);

        register(PROP_OWNER, EVT_SENT, ID_CHANGE_OWNER_REQUEST_SENT);
        register(PROP_OWNER, EVT_ACCEPTED, ID_CHANGE_OWNER_REQUEST_ACCEPTED);
        register(PROP_OWNER, EVT_REJECTED, ID_CHANGE_OWNER_REQUEST_REJECTED);
        register(PROP_OWNER, EVT_EXPIRED, ID_CHANGE_OWNER_REQUEST_EXPIRED);

        //Draft Survey 200
        register(PROP_NA, EVT_OPENED, ID_SURVEY_LOADED);
        register(PROP_PROPERTIES, EVT_SAVED, ID_PROPERTIES);
        register(PROP_USEFUL_LINK, EVT_ADDED, ID_USEFUL_LINK_ADD);
        register(PROP_USEFUL_LINK, EVT_REMOVED, ID_USEFUL_LINK_REMOVE);
        register(PROP_BACKGROUND_DOCUMENT, EVT_ADDED, ID_BACKGROUND_DOC_ADD);
        register(PROP_BACKGROUND_DOCUMENT, EVT_REMOVED, ID_BACKGROUND_DOC_REMOVE);
        register(PROP_TITLE, EVT_MODIFIED, ID_SURVEY_TITLE);
        register(PROP_PIVOT_LANGUAGE, EVT_MODIFIED, ID_MAIN_LANGUAGE);
        register(PROP_CONTACT, EVT_MODIFIED, ID_CONTACT_INFO);
        register(PROP_AUTOPUBLISH, EVT_MODIFIED, ID_AUTO_SUBMIT);
        register(PROP_START_DATE, EVT_MODIFIED, ID_START_DATE);
        register(PROP_END_DATE, EVT_MODIFIED, ID_END_DATE);
        register(PROP_LOGO, EVT_MODIFIED, ID_LOGO);
        register(PROP_SKIN, EVT_MODIFIED, ID_SKIN);
        register(PROP_AUTO_NUMBERING_SECTIONS, EVT_MODIFIED, ID_AUTO_NUM_SECTIONS);
        register(PROP_AUTO_NUMBERING_QUESTIONS, EVT_MODIFIED, ID_AUTO_NUM_QUESTIONS);
        register(PROP_ELEMENT_ORDER, EVT_MODIFIED, ID_ELEMENT_ORDER);
        register(PROP_SURVEY_ELEMENT, EVT_ADDED, ID_ELEMENT_ADDED);
        register(PROP_SURVEY_ELEMENT, EVT_DELETED, ID_ELEMENT_REMOVED);
        register(PROP_SURVEY_ELEMENT, EVT_MODIFIED,  ID_ELEMENT_UPDATED);
        register(PROP_TRANSLATION, EVT_ADDED, ID_TRANSLATION_ADDED);
        register(PROP_TRANSLATION, EVT_DELETED, ID_TRANSLATION_REMOVED);
        register(PROP_TRANSLATION, EVT_ENABLED, ID_TRANSLATION_ENABLED);
        register(PROP_TRANSLATION, EVT_DISABLED, ID_TRANSLATION_DISABLED);
        register(PROP_TRANSLATION, EVT_MODIFIED, ID_TRANSLATION_MODIFIED);
        register(PROP_TRANSLATION, EVT_REQUESTED, ID_MACHINE_TRANSLATION);
        register(PROP_CONFIRMATION_PAGE, EVT_MODIFIED, ID_CONFIRM_PAGE);
        register(PROP_ESCAPE_PAGE, EVT_MODIFIED, ID_ESCAPE_PAGE);

        //Results 300
        register(PROP_EXPORT, EVT_RETURNED, ID_EXPORT_FINISHED);
        register(PROP_EXPORT, EVT_DELETED, ID_EXPORT_DELETED);
        register(PROP_PUBLISH_INDIVIDUAL, EVT_MODIFIED, ID_CONTENT_PUBLICATION);
        register(PROP_PUBLISH_CHARTS, EVT_MODIFIED, ID_CHARTS_PUBLICATION);
        register(PROP_PUBLISH_STATISTICS, EVT_MODIFIED, ID_STATISTICS_PUBLICATION);
        register(PROP_PUBLIC_SEARCH, EVT_MODIFIED, ID_PUBLIC_RESULTS_SEARCH);
        register(PROP_PUBLISH_QUESTION_SELECTION, EVT_MODIFIED, ID_PUBLISH_QUESTION_SET);
        register(PROP_PUBLISH_ANSWER_SELECTION, EVT_MODIFIED, ID_PUBLISH_ANSWER_SET);
        register(PROP_EXPORT_STATISTICS, EVT_STARTED, ID_STATISTICS_EXPORT);
        register(PROP_EXPORT_CONTENT, EVT_STARTED, ID_CONTENT_EXPORT);
        register(PROP_EXPORT_CHARTS, EVT_STARTED, ID_CHART_EXPORT);
        register(PROP_PUBLISH_UPLOADED_ELEMENTS, EVT_MODIFIED, ID_UPLOADED_ELEMENTS_PUBLISH);
        register(PROP_EXPORT_UPLOADED_ELEMENTS, EVT_STARTED, ID_UPLOADED_ELEMENTS_DOWNLOAD);
        register(PROP_DELETE_COLUMN, EVT_DELETED, ID_BLANK_ANSWERS);

        register(PROP_NA, EVT_OPENED, ID_OPEN_QUORUM);
        register(PROP_NA, EVT_OPENED, ID_DISPLAY_RESULTS);
        register(PROP_NA, EVT_OPENED, ID_ALLOCATE_SEATS);
        register(PROP_NA, EVT_STARTED, ID_EXPORT_SEATS);

        register(PROP_NA, EVT_OPENED, ID_RESULTS_ACCESS);

        //Activities 312
        register(PROP_EXPORT_ACTIVITIES, EVT_STARTED, ID_ACTIVITY_EXPORT);

        //Contributions and Test Contributions 400
        register(PROP_NA, EVT_DELETED, ID_CONTRIBUTION_DELETE, ID_TEST_DELETE);
        register(PROP_NA, EVT_SUBMITTED, ID_CONTRIBUTION_SUBMIT, ID_TEST_SUBMIT, ID_DRAFT_CONTRIBUTION_SUBMIT);
        register(PROP_NA, EVT_MODIFIED, ID_CONTRIBUTION_EDIT, ID_TEST_EDIT);

        //Guest Lists 500
        register(PROP_TOKEN_CONTACTS_DEPARTMENT, EVT_CREATED, ID_GUEST_LIST_CREATED);
        register(PROP_TOKEN_CONTACTS_DEPARTMENT, EVT_DELETED, ID_GUEST_LIST_REMOVED);
        register(PROP_TOKEN_CONTACTS_DEPARTMENT, EVT_PAUSED, ID_GUEST_LIST_PAUSED);
        register(PROP_TOKEN_CONTACTS_DEPARTMENT, EVT_STARTED, ID_GUEST_LIST_STARTED);
        register(PROP_TOKEN_CONTACTS_DEPARTMENT, EVT_MODIFIED, ID_GUEST_LIST_MODIFIED);
        register(PROP_INVITATIONS, EVT_SENT, ID_GUEST_LIST_EMAIL);
        register(PROP_INVITATIONS, EVT_MODIFIED, ID_GUEST_LIST_CONTACT_CHANGED);

        register(PROP_TOKEN_CONTACTS_DEPARTMENT, EVT_ADDED, ID_GUEST_LIST_VOTER_ADDED);
        register(PROP_TOKEN_CONTACTS_DEPARTMENT, EVT_REMOVED, ID_GUEST_LIST_VOTER_REMOVED);

        //Privileges 600
        register(PROP_NA, EVT_ADDED, ID_DELEGATE_MANAGER_ADD);
        register(PROP_NA, EVT_MODIFIED, ID_PRIVILEGES_EDIT);
        register(PROP_NA, EVT_REMOVED, ID_PRIVILEGES_DELETE);

        //Messages 700
        register(PROP_END_NOTIFICATION_MESSAGE, EVT_SENT, ID_END_NOTIFICATION_SENT);

        //Comments 800
        register(PROP_NA, EVT_MODIFIED, ID_COMMENT_EDITED);
        register(PROP_NA, EVT_DELETED, ID_COMMENT_DELETED);
    }


    public static String getObjectFromId(int id){
        for (Map.Entry<String,Range<Integer>> entry : objectsMap.entrySet()){
            if (entry.getValue().contains(id)){
                return entry.getKey();
            }
        }
        return "";
    }

    public static Range<Integer> getObjectRange(String object){
        if (objectsMap.containsKey(object)){
            return objectsMap.get(object);
        }
        return Range.between(0, 0);
    }

    public static String[] getAllObjects(){
        return objectsMap.keySet().toArray(new String[0]);
    }

    public static Integer[] getPropertyIds(String property){
        if (propertyToIdsMap.containsKey(property)){
            return propertyToIdsMap.get(property).toArray(new Integer[0]);
        }
        return new Integer[0];
    }

    public static String getPropertyFromId(int id){
        if (idToEntryMap.containsKey(id)){
            return idToEntryMap.get(id).getProperty();
        }

        return PROP_NA;
    }

    public static String getPropertyFromId(int id, String type){
        String prop = getPropertyFromId(id);
        if (PROP_TOKEN_CONTACTS_DEPARTMENT.equals(prop) && type != null){
            return type;
        }
        return prop;
    }

    public static String[] getAllProperties(){
        return propertyToIdsMap.keySet().toArray(new String[0]);
    }

    public static Integer[] getEventIds(String event){
        if (eventToIdsMap.containsKey(event)){
            return eventToIdsMap.get(event).toArray(new Integer[0]);
        }
        return new Integer[0];
    }

    public static String getEventFromId(int id){
        if (idToEntryMap.containsKey(id)){
            return idToEntryMap.get(id).getEvent();
        }

        return "";
    }

    public static String[] getAllEvents(){
        return eventToIdsMap.keySet().toArray(new String[0]);
    }

    public static Integer[] getAllActivityIds(){
        return sortedActivityIds;
    }

    private static void registerObject(String name, int lowerBound, int upperBound){
        objectsMap.put(name, Range.between(lowerBound, upperBound));
    }


    private static void register(String property, String event, int... logIds){

        if (property == null)
            property = PROP_NA;

        for (int logId : logIds){
            String object = getObjectFromId(logId);
            ActivityRegistryEntry entry = new ActivityRegistryEntry(property, event, object, logId);
            mapEntry(entry);
        }
    }

    private static void mapEntry(ActivityRegistryEntry entry){

        idToEntryMap.put(entry.getId(), entry);

        if (!propertyToIdsMap.containsKey(entry.getProperty())){
            propertyToIdsMap.put(entry.getProperty(), new ArrayList<>());
        }

        propertyToIdsMap.get(entry.getProperty()).add(entry.getId());


        if (!eventToIdsMap.containsKey(entry.getEvent())){
            eventToIdsMap.put(entry.getEvent(), new ArrayList<>());
        }

        eventToIdsMap.get(entry.getEvent()).add(entry.getId());
    }
}
