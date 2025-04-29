package com.colbertlum.entity;

public class MoveOutFactory {

    public static void setProductName(ReturnMoveOut moveOut, String productName) {
        if(moveOut instanceof ReturnShopeeMoveOut) {
            ReturnShopeeMoveOut returnShopeeMoveOut = (ReturnShopeeMoveOut) moveOut;
            returnShopeeMoveOut.setProductName(productName);
        }
        if(moveOut instanceof ReturnTikTokMoveOut) {
            ReturnTikTokMoveOut returnTikTokMoveOut = (ReturnTikTokMoveOut) moveOut;
            returnTikTokMoveOut.setProductName(productName);
        }
    }

    public static void setProductName(MoveOut moveOut, String productName) {
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            shopeeMoveOut.setProductName(productName);
        }
        if(moveOut instanceof TikTokMoveOut) {
            TikTokMoveOut tikTokMoveOut = (TikTokMoveOut) moveOut;
            tikTokMoveOut.setProductName(productName);
        }
    }

    public static String getProductName(MoveOut moveOut){
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return shopeeMoveOut.getProductName();
        }
        if(moveOut instanceof TikTokMoveOut) {
            TikTokMoveOut tikTokMoveOut = (TikTokMoveOut) moveOut;
            return tikTokMoveOut.getProductName();
        }
        return null;
    }

    public static String getProductName(ReturnMoveOut returnMoveOut){
        if(returnMoveOut instanceof ReturnShopeeMoveOut) {
            ReturnShopeeMoveOut returnShopeeMoveOut = (ReturnShopeeMoveOut) returnMoveOut;
            return returnShopeeMoveOut.getProductName();
        }
        if(returnMoveOut instanceof ReturnTikTokMoveOut) {
            ReturnTikTokMoveOut returnTikTokMoveOut = (ReturnTikTokMoveOut) returnMoveOut;
            return returnTikTokMoveOut.getProductName();
        }
        return null;
    }


    public static void setVariationName(ReturnMoveOut moveOut, String variationName) {
        if(moveOut instanceof ReturnShopeeMoveOut) {
            ReturnShopeeMoveOut returnShopeeMoveOut = (ReturnShopeeMoveOut) moveOut;
            returnShopeeMoveOut.setVariationName(variationName);
        }
        if(moveOut instanceof ReturnTikTokMoveOut) {
            ReturnTikTokMoveOut returnTikTokMoveOut = (ReturnTikTokMoveOut) moveOut;
            returnTikTokMoveOut.setVariationName(variationName);
        }
    }

    public static String getVariationName(MoveOut moveOut){
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return shopeeMoveOut.getVariationName();
        }
        if(moveOut instanceof TikTokMoveOut) {
            TikTokMoveOut tikTokMoveOut = (TikTokMoveOut) moveOut;
            return tikTokMoveOut.getVariationName();
        }
        return null;
    }

    public static String getVariationName(ReturnMoveOut returnMoveOut){
        if(returnMoveOut instanceof ReturnShopeeMoveOut) {
            ReturnShopeeMoveOut returnShopeeMoveOut = (ReturnShopeeMoveOut) returnMoveOut;
            return returnShopeeMoveOut.getVariationName();
        }
        if(returnMoveOut instanceof ReturnTikTokMoveOut) {
            ReturnTikTokMoveOut returnTikTokMoveOut = (ReturnTikTokMoveOut) returnMoveOut;
            return returnTikTokMoveOut.getVariationName();
        }
        return null;
    }

    public static Double getFinalPrice(MoveOut moveOut) {
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return shopeeMoveOut.getFinalPrice();
        }
        if(moveOut instanceof TikTokMoveOut) {
            TikTokMoveOut tikTokMoveOut = (TikTokMoveOut) moveOut;
            return tikTokMoveOut.getFinalPrice();
        }
        return 0d;
    }

    public static Double getSKUSubTotal(MoveOut moveOut) {
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return shopeeMoveOut.getProductSubtotal();
        }
        if(moveOut instanceof TikTokMoveOut) {
            TikTokMoveOut tikTokMoveOut = (TikTokMoveOut) moveOut;
            return tikTokMoveOut.getSKUsubtotalAfterDiscount();
        }
        return 0d;
    }
}
