package com.reitplace.tvalue;

public class TagProcessor {

    private Config config = null;

    public TagProcessor(Config config) {
        this.config = config;
    }
    public InputTag process(String input) {
        InputTag iTag = null;

        // Check the input string and identify the Tag type
        iTag = isValidUser(input);
        if (iTag != null && iTag.getTagType() != Constants.TagTypeInvalid) {
            Console.debug("User Tag identified => "+iTag.getTagType());
            return iTag;
        }
        iTag = isValidLocation(input);
        if (iTag != null && iTag.getTagType() != Constants.TagTypeInvalid) {
            Console.debug("Location Tag identified => "+iTag.getTagType());
            return iTag;
        }
        // NOTE UPC QTY (Is an Action) and treated special so should be above isValidAction()
        iTag = isValidUPCQty(input);
        if (iTag != null && iTag.getTagType() != Constants.TagTypeInvalid) {
            Console.debug("Qty Tag identified => "+iTag.getTagType());
            return iTag;
        }
        iTag = isValidUPC(input);
        if (iTag != null && iTag.getTagType() != Constants.TagTypeInvalid) {
            Console.debug("UPC Tag identified => "+iTag.getTagType());
            return iTag;
        }
        iTag = isValidAction(input);
        if (iTag != null && iTag.getTagType() != Constants.TagTypeInvalid) {
            Console.debug("Action Tag identified => "+iTag.getTagType());
            return iTag;
        }




        return iTag;
    }

    /**
     * Example user tag: HGTVUSR000287
     * @param input String represents the user tag
     * @return InputTag
     */
    private InputTag isValidUser(String input) {
        InputTag iTag =new InputTag(Constants.TagTypeInvalid, null);

        Console.debug("User "+input+" (length => "+input.length()+")");
        if (input.toUpperCase().startsWith(Constants.HG_IDENTIFIER) && input.length() == Constants.HG_TAG_LENGTH
                && Constants.HG_USER_IDENTIFIER.equalsIgnoreCase(input.substring(4,7)) ) {

            Console.debug("UserID: input => "+input);
            iTag = new InputTag(Constants.TagTypeUser, input.substring(7,13));
        }

        return iTag;
    }

    /**
     * Example location tag: HGTVSA1000A12
     * @param input String represents the location tag
     * @return InputTag
     */
    private InputTag isValidLocation(String input) {
        InputTag iTag = new InputTag(Constants.TagTypeInvalid, null);

        Console.debug("Location "+input+"(input length => "+input.length()+")");
        if (input.toUpperCase().startsWith(Constants.HG_IDENTIFIER) && input.length() == Constants.HG_TAG_LENGTH) {
            String locCode = input.substring(4,5);
            if (config.locationCodeMap.containsKey(locCode)) {
                Console.debug("Location code from input => "+locCode);
                String primaryTag =  input.substring(10,11);
                String secondaryTag = input.substring(4,5);

                if (config.isLocationComplete(primaryTag + secondaryTag)) {

                    iTag = new InputTag(Constants.TagTypeLocation,
                            new LocationV2(primaryTag, input.substring(11,13), secondaryTag, input.substring(5,7)));
                } else {
                    Console.debug("Invalid Location (primary="+primaryTag+" & secondary="+secondaryTag+") tag combination => ");
                }
            }

        }

        return iTag;
    }

    /**
     * Example UPC tag: 123456789001
     * @param input String represents the UPC code/tag
     * @return InputTag
     */
    private InputTag isValidUPC(String input) {
        InputTag iTag = new InputTag(Constants.TagTypeInvalid, null);

        Console.debug("UPC "+input+"(input length => "+input.length()+")");
        if (!input.toUpperCase().startsWith(Constants.HG_IDENTIFIER) && input.length() > Constants.UPC_TAG_MIN_LENGTH) {
            Console.debug("UPC code from input => "+input);
            iTag = new InputTag(Constants.TagTypeUPC, input);

        }

        return iTag;
    }

    /**
     * UPC Quantity can be input as a Tag or direct value
     * Example UPC Quantity tag: 25 or HGTV000Q25000
     * @param input String represents the UPC Qty code/tag
     * @return InputTag
     */
    private InputTag isValidUPCQty(String input) {
        InputTag iTag =new InputTag(Constants.TagTypeInvalid, null);

        Console.debug("UPC Qty "+input+"(input length => "+input.length()+")");
        if (input.toUpperCase().startsWith(Constants.HG_IDENTIFIER) && input.length() == Constants.HG_TAG_LENGTH) {
            Console.debug("UPC code from input => "+input);
            if (Constants.ACTION_QTY.equals(input.substring(7,8))) {
                try {
                    int qty = Integer.parseInt(input.substring(8,10));
                    iTag = new InputTag(Constants.TagTypeQty, qty);
                    Console.debug("UPC qty number from input (1) => "+qty);
                } catch (NumberFormatException ne) {
                    Console.out("Error!! Either valid Quantity tag or Invalid Quantity. A number is expected.");
                }
            }
        } else if (input.length() < Constants.UPC_QTY_TAG_MAX_LENGTH) {
            try {
                int qty = Integer.parseInt(input);
                iTag = new InputTag(Constants.TagTypeQty, qty);
                Console.debug("UPC qty number from input (2) => "+qty);
            } catch (NumberFormatException ne) {
                Console.out("Error!! Invalid Quantity. A number is expected.");
            }
        }

        return iTag;
    }

    /**
     * Example Action tag: HGTV000X00000
     * @param input String represents the Action tag
     * @return InputTag
     */
    private InputTag isValidAction(String input) {
        InputTag iTag = new InputTag(Constants.TagTypeInvalid, null);

        Console.debug("Location "+input+"(input length => "+input.length()+")");
        if (input.toUpperCase().startsWith(Constants.HG_IDENTIFIER) && input.length() == Constants.HG_TAG_LENGTH) {
            String actionCode = input.substring(7,8);
            if (config.actionCodesMap.containsKey(actionCode)) {
                Console.debug("Action code from input => "+actionCode);
                iTag = new InputTag(Constants.TagTypeAction, actionCode);
            }
        }

        return iTag;
    }
}
