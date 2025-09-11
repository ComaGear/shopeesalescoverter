package com.colbertlum.Imputer.Utils;

import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnShopeeMoveOut;
import com.colbertlum.entity.ReturnTikTokMoveOut;
import com.colbertlum.entity.ShopeeMoveOut;
import com.colbertlum.entity.ShopeeOrder;
import com.colbertlum.entity.TikTokMoveOut;
import com.colbertlum.entity.TikTokOrder;

public class MoveOutFactory {

    public static double getReturnedQuantity(MoveOut moveOut) {
        if(moveOut == null) return 0;
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return shopeeMoveOut.getReturnedQuantity();
        } else if(moveOut instanceof TikTokMoveOut) {
            // TODO add a way to retrieve returnedQuantity from TikTokMoveOut;
            return 0;
        }
        return 0;
    }

    public static boolean isRequestReturnRefundApproved(MoveOut moveOut) {
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return shopeeMoveOut.isReturnRefundRequest();
        }
        return false;
    }

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

    public static String getPlatformOrderStatus(MoveOut moveOut) {
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return ((ShopeeOrder)shopeeMoveOut.getOrder()).getStatus();
        }
        if(moveOut instanceof TikTokMoveOut) {
            TikTokMoveOut tikTokMoveOut = (TikTokMoveOut) moveOut;
            return ((TikTokOrder) tikTokMoveOut.getOrder()).getStatus();
        }
        return null;
    }

    public static String getPlatform(MoveOut moveOut) {
        if(moveOut instanceof ShopeeMoveOut) {
            return "Shopee";
        }
        if(moveOut instanceof TikTokMoveOut) {
            return "TikTok";
        }
        return null;
    }
    
    public static String getPlatform(ReturnMoveOut moveOut) {
        if(moveOut instanceof ReturnShopeeMoveOut) {
            return "Shopee";
        }
        if(moveOut instanceof ReturnTikTokMoveOut) {
            return "TikTok";
        }
        return null;
    }

    public static double getPlatformDiscount(MoveOut moveOut) {
        if(moveOut instanceof ShopeeMoveOut) {
            ShopeeMoveOut shopeeMoveOut = (ShopeeMoveOut) moveOut;
            return shopeeMoveOut.getPlatformDiscount();
        }
        if(moveOut instanceof TikTokMoveOut) {
            TikTokMoveOut tikTokMoveOut = (TikTokMoveOut) moveOut;
            return tikTokMoveOut.getSKUplatformDiscount();
        }
        return 0d;
    }

    public static double getPlatformDiscount(ReturnMoveOut moveOut) {
        if(moveOut instanceof ReturnShopeeMoveOut) {
            ReturnShopeeMoveOut shopeeMoveOut = (ReturnShopeeMoveOut) moveOut;
            return shopeeMoveOut.getPlatformDiscount();
        }
        if(moveOut instanceof ReturnTikTokMoveOut) {
            ReturnTikTokMoveOut tikTokMoveOut = (ReturnTikTokMoveOut) moveOut;
            return tikTokMoveOut.getSKUplatformDiscount();
        }
        return 0d;
    }
}
