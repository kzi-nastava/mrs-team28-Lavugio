package model.user;

public class BlockableAccount extends Account {
    private boolean isBlocked;
    private String blockReason;

    public BlockableAccount() {

    }

    public BlockableAccount(boolean isBlocked, String blockReason) {
        this.isBlocked = isBlocked;
        this.blockReason = blockReason;
    }

    public BlockableAccount(String name, String lastName, String email, String password, String profilePhoto, boolean isBlocked, String blockReason) {
        super(name, lastName, email, password, profilePhoto);
        this.isBlocked = isBlocked;
        this.blockReason = blockReason;
    }

    public BlockableAccount(String name, String lastName, String email, String password, String profilePhoto, long id, boolean isBlocked, String blockReason) {
        super(name, lastName, email, password, profilePhoto, id);
        this.isBlocked = isBlocked;
        this.blockReason = blockReason;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }
}
