package com.example.lavugio_mobile.models;

/**
 * Response model for checking if a user can order a ride.
 */
public class CanOrderRideResponse {
    private boolean isInRide;
    private BlockInfo block;

    public static class BlockInfo {
        private boolean blocked;
        private String reason;

        public boolean isBlocked() { return blocked; }
        public void setBlocked(boolean blocked) { this.blocked = blocked; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public boolean isInRide() { return isInRide; }
    public void setInRide(boolean inRide) { isInRide = inRide; }

    public BlockInfo getBlock() { return block; }
    public void setBlock(BlockInfo block) { this.block = block; }
}
