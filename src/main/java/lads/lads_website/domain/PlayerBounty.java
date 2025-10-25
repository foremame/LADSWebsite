package lads.lads_website.domain;

import jakarta.persistence.*;

@Entity
@Table
public class PlayerBounty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="bounty_reward_id")
    private BountyReward bountyReward;

    public PlayerBounty() {
    }

    public PlayerBounty(Long id, Player player, BountyReward bountyReward) {
        this.id = id;
        this.player = player;
        this.bountyReward = bountyReward;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BountyReward getBountyReward() {
        return bountyReward;
    }

    public void setBountyReward(BountyReward bountyReward) {
        this.bountyReward = bountyReward;
    }
}
