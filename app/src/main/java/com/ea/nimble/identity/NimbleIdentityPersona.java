package com.ea.nimble.identity;

import com.ea.nimble.Log;

import java.util.Date;
import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPersona.class */
public class NimbleIdentityPersona {
    private String dateCreated;
    private String displayName;
    private Date expiryTime;
    private String isVisible;
    private String lastAuthenticated;
    private String name;
    private String namespaceName;
    private long personaId;
    private String pidId;
    private PersonaPrivacyLevel showPersona;
    private PersonaStatus status;
    private PersonaStatusReasonCodes statusReasonCode;

    /* renamed from: com.ea.nimble.identity.NimbleIdentityPersona$1  reason: invalid class name */
    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPersona$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaPrivacyLevel = new int[PersonaPrivacyLevel.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus;
        static final /* synthetic */ int[] $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes;

        static {
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaPrivacyLevel[PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_NO_ONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaPrivacyLevel[PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_EVERYONE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaPrivacyLevel[PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_FRIENDS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaPrivacyLevel[PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_FRIENDS_OF_FRIENDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes = new int[PersonaStatusReasonCodes.values().length];
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_ONE.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_REACTIVATED_CUSTOMER.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_INVALID_EMAIL.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_PRIVACY_POLICY.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_PARENTS_REQUEST.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_GENERAL.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_HARASSMENT.ordinal()] = 7;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_MACROING.ordinal()] = 8;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_EXPLOITATION.ordinal()] = 9;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_FRAUD.ordinal()] = 10;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_CUSTOMER_OPT_OUT.ordinal()] = 11;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_CUSTOMER_UNDER_AGE.ordinal()] = 12;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_EMAIL_CONFIRMATION_REQUIRED.ordinal()] = 13;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_MISTYPED_ID.ordinal()] = 14;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_ABUSED_ID.ordinal()] = 15;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_DEACTIVATED_EMAIL_LINK.ordinal()] = 16;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_DEACTIVATED_CS.ordinal()] = 17;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_CLAIMED_BY_TRUE_OWNER.ordinal()] = 18;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_BANNED.ordinal()] = 19;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_DEACTIVATED_AFFILIATE.ordinal()] = 20;
            } catch (NoSuchFieldError e24) {
            }
            $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus = new int[PersonaStatus.values().length];
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus[PersonaStatus.PERSONA_STATUS_PENDING.ordinal()] = 1;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus[PersonaStatus.PERSONA_STATUS_ACTIVE.ordinal()] = 2;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus[PersonaStatus.PERSONA_STATUS_DEACTIVATED.ordinal()] = 3;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus[PersonaStatus.PERSONA_STATUS_DISABLED.ordinal()] = 4;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus[PersonaStatus.PERSONA_STATUS_DELETED.ordinal()] = 5;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus[PersonaStatus.PERSONA_STATUS_BANNED.ordinal()] = 6;
            } catch (NoSuchFieldError e30) {
            }
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPersona$PersonaPrivacyLevel.class */
    public enum PersonaPrivacyLevel {
        PERSONA_PRIVACY_LEVEL_NONE,
        PERSONA_PRIVACY_LEVEL_NO_ONE,
        PERSONA_PRIVACY_LEVEL_EVERYONE,
        PERSONA_PRIVACY_LEVEL_FRIENDS,
        PERSONA_PRIVACY_LEVEL_FRIENDS_OF_FRIENDS
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPersona$PersonaStatus.class */
    public enum PersonaStatus {
        PERSONA_STATUS_NONE,
        PERSONA_STATUS_PENDING,
        PERSONA_STATUS_ACTIVE,
        PERSONA_STATUS_DEACTIVATED,
        PERSONA_STATUS_DISABLED,
        PERSONA_STATUS_DELETED,
        PERSONA_STATUS_BANNED
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPersona$PersonaStatusReasonCodes.class */
    public enum PersonaStatusReasonCodes {
        PERSONA_STATUS_REASON_CODES_NONE,
        PERSONA_STATUS_REASON_CODES_ONE,
        PERSONA_STATUS_REASON_CODES_REACTIVATED_CUSTOMER,
        PERSONA_STATUS_REASON_CODES_INVALID_EMAIL,
        PERSONA_STATUS_REASON_CODES_PRIVACY_POLICY,
        PERSONA_STATUS_REASON_CODES_PARENTS_REQUEST,
        PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_GENERAL,
        PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_HARASSMENT,
        PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_MACROING,
        PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_EXPLOITATION,
        PERSONA_STATUS_REASON_CODES_SUSPENDED_FRAUD,
        PERSONA_STATUS_REASON_CODES_CUSTOMER_OPT_OUT,
        PERSONA_STATUS_REASON_CODES_CUSTOMER_UNDER_AGE,
        PERSONA_STATUS_REASON_CODES_EMAIL_CONFIRMATION_REQUIRED,
        PERSONA_STATUS_REASON_CODES_MISTYPED_ID,
        PERSONA_STATUS_REASON_CODES_ABUSED_ID,
        PERSONA_STATUS_REASON_CODES_DEACTIVATED_EMAIL_LINK,
        PERSONA_STATUS_REASON_CODES_DEACTIVATED_CS,
        PERSONA_STATUS_REASON_CODES_CLAIMED_BY_TRUE_OWNER,
        PERSONA_STATUS_REASON_CODES_BANNED,
        PERSONA_STATUS_REASON_CODES_DEACTIVATED_AFFILIATE
    }

    NimbleIdentityPersona() {
    }

    public NimbleIdentityPersona(Map<String, Object> map, Date date) {
        this.personaId = getLongFromObject(map.get("personaId"));
        this.pidId = map.get("pidId").toString();
        this.displayName = (String) map.get("displayName");
        this.name = (String) map.get("name");
        this.namespaceName = (String) map.get("namespaceName");
        this.status = toEnumPersonaStatus((String) map.get("status"));
        this.statusReasonCode = toEnumPersonaStatusReasonCodes((String) map.get("statusReasonCode"));
        this.showPersona = toEnumPersonaPrivacyLevel((String) map.get("showPersona"));
        this.dateCreated = (String) map.get("dateCreated");
        this.lastAuthenticated = (String) map.get("lastAuthenticated");
        Object obj = map.get("isVisible");
        if (obj != null) {
            if (obj instanceof Boolean) {
                if (((Boolean) obj) == Boolean.TRUE) {
                    this.isVisible = "true";
                } else {
                    this.isVisible = "false";
                }
            } else if (obj instanceof String) {
                this.isVisible = (String) obj;
            }
        }
        this.expiryTime = date;
    }

    private long getLongFromObject(Object obj) {
        if (obj instanceof Long) {
            return ((Long) obj).longValue();
        }
        if (obj instanceof Integer) {
            return (long) ((Integer) obj).intValue();
        }
        Log.Helper.LOGES("Identity", "Can't convert object of type " + obj.getClass().getName() + " to long", new Object[0]);
        return 0;
    }

    public static PersonaPrivacyLevel toEnumPersonaPrivacyLevel(String str) {
        PersonaPrivacyLevel personaPrivacyLevel = PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_NONE;
        if (str.equalsIgnoreCase("NO_ONE")) {
            personaPrivacyLevel = PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_NO_ONE;
        } else if (str.equalsIgnoreCase("EVERYONE")) {
            return PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_EVERYONE;
        } else {
            if (str.equalsIgnoreCase("FRIENDS")) {
                return PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_FRIENDS;
            }
            if (str.equalsIgnoreCase("FRIENDS_OF_FRIENDS")) {
                return PersonaPrivacyLevel.PERSONA_PRIVACY_LEVEL_FRIENDS_OF_FRIENDS;
            }
        }
        return personaPrivacyLevel;
    }

    public static PersonaStatus toEnumPersonaStatus(String str) {
        PersonaStatus personaStatus = PersonaStatus.PERSONA_STATUS_NONE;
        if (str.equalsIgnoreCase("PENDING")) {
            personaStatus = PersonaStatus.PERSONA_STATUS_PENDING;
        } else if (str.equalsIgnoreCase("ACTIVE")) {
            return PersonaStatus.PERSONA_STATUS_ACTIVE;
        } else {
            if (str.equalsIgnoreCase("DEACTIVATED")) {
                return PersonaStatus.PERSONA_STATUS_DEACTIVATED;
            }
            if (str.equalsIgnoreCase("DISABLE")) {
                return PersonaStatus.PERSONA_STATUS_DISABLED;
            }
            if (str.equalsIgnoreCase("DELETED")) {
                return PersonaStatus.PERSONA_STATUS_DELETED;
            }
            if (str.equalsIgnoreCase("BANNED")) {
                return PersonaStatus.PERSONA_STATUS_BANNED;
            }
        }
        return personaStatus;
    }

    public static PersonaStatusReasonCodes toEnumPersonaStatusReasonCodes(String str) {
        PersonaStatusReasonCodes personaStatusReasonCodes = PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_NONE;
        if (str.equalsIgnoreCase("CODES_ONE")) {
            personaStatusReasonCodes = PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_ONE;
        } else if (str.equalsIgnoreCase("REACTIVATED_CUSTOMER")) {
            return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_REACTIVATED_CUSTOMER;
        } else {
            if (str.equalsIgnoreCase("INVALID_EMAIL")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_INVALID_EMAIL;
            }
            if (str.equalsIgnoreCase("PRIVACY_POLICY")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_PRIVACY_POLICY;
            }
            if (str.equalsIgnoreCase("PARENTS_REQUEST")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_PARENTS_REQUEST;
            }
            if (str.equalsIgnoreCase("SUSPENDED_MISCONDUCT_GENERAL")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_GENERAL;
            }
            if (str.equalsIgnoreCase("SUSPENDED_MISCONDUCT_HARASSMENT")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_HARASSMENT;
            }
            if (str.equalsIgnoreCase("SUSPENDED_MISCONDUCT_MACROING")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_MACROING;
            }
            if (str.equalsIgnoreCase("SUSPENDED_MISCONDUCT_EXPLOITATION")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_MISCONDUCT_EXPLOITATION;
            }
            if (str.equalsIgnoreCase("SUSPENDED_FRAUD")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_SUSPENDED_FRAUD;
            }
            if (str.equalsIgnoreCase("CUSTOMER_OPT_OUT")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_CUSTOMER_OPT_OUT;
            }
            if (str.equalsIgnoreCase("CUSTOMER_UNDER_AGE")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_CUSTOMER_UNDER_AGE;
            }
            if (str.equalsIgnoreCase("EMAIL_CONFIRMATION_REQUIRED")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_EMAIL_CONFIRMATION_REQUIRED;
            }
            if (str.equalsIgnoreCase("MISTYPED_ID")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_MISTYPED_ID;
            }
            if (str.equalsIgnoreCase("ABUSED_ID")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_ABUSED_ID;
            }
            if (str.equalsIgnoreCase("DEACTIVATED_EMAIL_LINK")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_DEACTIVATED_EMAIL_LINK;
            }
            if (str.equalsIgnoreCase("DEACTIVATED_CS")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_DEACTIVATED_CS;
            }
            if (str.equalsIgnoreCase("CLAIMED_BY_TRUE_OWNER")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_CLAIMED_BY_TRUE_OWNER;
            }
            if (str.equalsIgnoreCase("BANNED")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_BANNED;
            }
            if (str.equalsIgnoreCase("DEACTIVATED_AFFILIATE")) {
                return PersonaStatusReasonCodes.PERSONA_STATUS_REASON_CODES_DEACTIVATED_AFFILIATE;
            }
        }
        return personaStatusReasonCodes;
    }

    public static String toStringPersonaPrivacyLevel(PersonaPrivacyLevel personaPrivacyLevel) {
        switch (AnonymousClass1.$SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaPrivacyLevel[personaPrivacyLevel.ordinal()]) {
            case 1:
                return "NO_ONE";
            case 2:
                return "EVERYONE";
            case 3:
                return "FRIENDS";
            case 4:
                return "FRIENDS_OF_FRIENDS";
            default:
                return "";
        }
    }

    public static String toStringPersonaStatus(PersonaStatus personaStatus) {
        switch (AnonymousClass1.$SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatus[personaStatus.ordinal()]) {
            case 1:
                return "PENDING";
            case 2:
                return "ACTIVE";
            case 3:
                return "DEACTIVATED";
            case 4:
                return "DISABLED";
            case 5:
                return "DELETED";
            case 6:
                return "BANNED";
            default:
                return "";
        }
    }

    public static String toStringPersonaStatusReasonCodes(PersonaStatusReasonCodes personaStatusReasonCodes) {
        switch (AnonymousClass1.$SwitchMap$com$ea$nimble$identity$NimbleIdentityPersona$PersonaStatusReasonCodes[personaStatusReasonCodes.ordinal()]) {
            case 1:
                return "CODES_ONE";
            case 2:
                return "REACTIVATED_CUSTOMER";
            case 3:
                return "INVALID_EMAIL";
            case 4:
                return "PRIVACY_POLICY";
            case 5:
                return "PARENTS_REQUEST";
            case 6:
                return "SUSPENDED_MISCONDUCT_GENERAL";
            case 7:
                return "SUSPENDED_MISCONDUCT_HARASSMENT";
            case 8:
                return "SUSPENDED_MISCONDUCT_MACROING";
            case 9:
                return "SUSPENDED_MISCONDUCT_EXPLOITATION";
            case 10:
                return "SUSPENDED_FRAUD";
            case 11:
                return "CUSTOMER_OPT_OUT";
            case 12:
                return "CUSTOMER_UNDER_AGE";
            case 13 /* 13 */:
                return "EMAIL_CONFIRMATION_REQUIRED";
            case 14:
                return "MISTYPED_ID";
            case 15 /* 15 */:
                return "ABUSED_ID";
            case 16:
                return "DEACTIVATED_EMAIL_LINK";
            case 17:
                return "DEACTIVATED_CS";
            case 18:
                return "CLAIMED_BY_TRUE_OWNER";
            case 19:
                return "BANNED";
            case 20:
                return "DEACTIVATED_AFFILIATE";
            default:
                return "";
        }
    }

    public String getDateCreated() {
        return this.dateCreated;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Date getExpiryTime() {
        return this.expiryTime;
    }

    public String getLastAuthenticated() {
        return this.lastAuthenticated;
    }

    public String getName() {
        return this.name;
    }

    public String getNamespaceName() {
        return this.namespaceName;
    }

    public long getPersonaId() {
        return this.personaId;
    }

    public String getPidId() {
        return this.pidId;
    }

    public PersonaPrivacyLevel getShowPersona() {
        return this.showPersona;
    }

    public PersonaStatusReasonCodes getStatusReasonCode() {
        return this.statusReasonCode;
    }

    public PersonaStatus getStauts() {
        return this.status;
    }

    public String getVisible() {
        return this.isVisible;
    }

    public void setDateCreated(String str) {
        this.dateCreated = str;
    }

    public void setDisplayName(String str) {
        this.displayName = str;
    }

    public void setLastAuthenticated(String str) {
        this.lastAuthenticated = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setNamespaceName(String str) {
        this.namespaceName = str;
    }

    public void setPersonaId(long j) {
        this.personaId = j;
    }

    public void setPidId(String str) {
        this.pidId = str;
    }

    public void setShowPersona(PersonaPrivacyLevel personaPrivacyLevel) {
        this.showPersona = personaPrivacyLevel;
    }

    public void setStatusReasonCode(PersonaStatusReasonCodes personaStatusReasonCodes) {
        this.statusReasonCode = personaStatusReasonCodes;
    }

    public void setStauts(PersonaStatus personaStatus) {
        this.status = personaStatus;
    }

    public void setVisible(String str) {
        this.isVisible = str;
    }
}
