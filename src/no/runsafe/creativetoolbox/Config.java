package no.runsafe.creativetoolbox;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

import java.util.List;

public class Config implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration config)
	{
		max_listed = config.getConfigValueAsInt("max_listed");
		noClean = config.getConfigValueAsList("clean.ignore");
		paintbrushName = config.getConfigValueAsString("paintbrushName");

		Message.wrongWorld = config.getConfigValueAsString("message.wroldWorld");
		Message.noWorldGuard = config.getConfigValueAsString("message.noWorldGuard");
		Message.noWorldDefined = config.getConfigValueAsString("message.noWorldDefined");
		Message.invalidLocation = config.getConfigValueAsString("message.invalidLocation");

		Message.Ai.pattern = config.getConfigValueAsString("message.ai.pattern");
		Message.Ai.needPermissionNoStaff = config.getConfigValueAsString("message.ai.needPermissionNoStaff");
		Message.Ai.needPermissionStaff = config.getConfigValueAsString("message.ai.needPermissionStaff");
		Message.Ai.plotsNotApproved = config.getConfigValueAsString("message.ai.plotsNotApproved");
		Message.Ai.findFreePlot = config.getConfigValueAsString("message.ai.findFreePlot");
		Message.Ai.claimCurrentPlot = config.getConfigValueAsString("message.ai.claimCurrentPlot");

		Message.Plot.invalid = config.getConfigValueAsString("message.plot.invalid");
		Message.Plot.invalid2 = config.getConfigValueAsString("message.plot.invalid2");
		Message.Plot.notFound = config.getConfigValueAsString("message.plot.notFound");
		Message.Plot.noPlotsOwned = config.getConfigValueAsString("message.plot.noPlotsOwned");
		Message.Plot.notOwner = config.getConfigValueAsString("message.plot.notOwner");

		Message.Plot.Approve.invalid = config.getConfigValueAsString("message.plot.approve.invalid");
		Message.Plot.Approve.fail = config.getConfigValueAsString("message.plot.approve.fail");
		Message.Plot.Approve.approvedFalse = config.getConfigValueAsString("message.plot.approve.approvedFalse");
		Message.Plot.Approve.approvedTrue = config.getConfigValueAsString("message.plot.approve.approvedTrue");
		Message.Plot.Approve.global = config.getConfigValueAsString("message.plot.approve.global");

		Message.Plot.Claim.failPreexisting = config.getConfigValueAsString("message.plot.claim.failPreexisting");
		Message.Plot.Claim.failNotClaimable = config.getConfigValueAsString("message.plot.claim.failNotClaimable");
		Message.Plot.Claim.failOtherNoPermissions = config.getConfigValueAsString("message.plot.claim.failOtherNoPermissions");
		Message.Plot.Claim.failPreviousNotApproved = config.getConfigValueAsString("message.plot.claim.failPreviousNotApproved");
		Message.Plot.Claim.fail = config.getConfigValueAsString("message.plot.claim.fail");
		Message.Plot.Claim.succeedSelf = config.getConfigValueAsString("message.plot.claim.succeedSelf");
		Message.Plot.Claim.succeedOther = config.getConfigValueAsString("message.plot.claim.succeedOther");

		Message.Plot.Clean.itemsCleaned = config.getConfigValueAsString("message.plot.clean.itemsCleaned");
		Message.Plot.Clean.listFormat = config.getConfigValueAsString("message.plot.clean.listFormat");
		Message.Plot.Clean.cleanFail = config.getConfigValueAsString("message.plot.clean.cleanFail");
		Message.Plot.Clean.cleanFailNoWorld = config.getConfigValueAsString("message.plot.clean.cleanFailNoWorld");
		Message.Plot.Clean.blocksCleaned = config.getConfigValueAsString("message.plot.clean.blocksCleaned");
		Message.Plot.Clean.cleanRoadsFail = config.getConfigValueAsString("message.plot.clean.cleanRoadsFail");
		Message.Plot.Clean.cleanRoadsSuccess = config.getConfigValueAsString("message.plot.clean.cleanRoadsSuccess");

		Message.Plot.Delete.failApproved = config.getConfigValueAsString("message.plot.delete.failApproved");
		Message.Plot.Delete.rightClick = config.getConfigValueAsString("message.plot.delete.rightClick");
		Message.Plot.Delete.success = config.getConfigValueAsString("message.plot.delete.success");

		Message.Plot.Extend.rightClick = config.getConfigValueAsString("message.plot.extend.rightClick");
		Message.Plot.Extend.failOverlap = config.getConfigValueAsString("message.plot.extend.failOverlap");
		Message.Plot.Extend.failError = config.getConfigValueAsString("message.plot.extend.failError");
		Message.Plot.Extend.success = config.getConfigValueAsString("message.plot.extend.success");

		Message.Plot.Teleport.findFailNoneLeft = config.getConfigValueAsString("message.plot.teleport.findFailNoneLeft");
		Message.Plot.Teleport.randomFailNoPlotsTagged = config.getConfigValueAsString("message.plot.teleport.randomFailNoPlotsTagged");
		Message.Plot.Teleport.failedNoList = config.getConfigValueAsString("message.plot.teleport.failedNoList");
		Message.Plot.Teleport.list = config.getConfigValueAsString("message.plot.teleport.list");
		Message.Plot.Teleport.success = config.getConfigValueAsString("message.plot.teleport.success");
		Message.Plot.Teleport.jump = config.getConfigValueAsString("message.plot.teleport.jump");

		Message.Plot.List.command = config.getConfigValueAsString("message.plot.list.command");

		Message.Plot.List.Interact.genericOwner = config.getConfigValueAsString("message.plot.list.interact.genericOwner");
		Message.Plot.List.Interact.genericMember = config.getConfigValueAsString("message.plot.list.interact.genericMember");
		Message.Plot.List.Interact.tags = config.getConfigValueAsString("message.plot.list.interact.tags");
		Message.Plot.List.Interact.member = config.getConfigValueAsString("message.plot.list.interact.member");
		Message.Plot.List.Interact.memberTime = config.getConfigValueAsString("message.plot.list.interact.memberTime");
		Message.Plot.List.Interact.memberNeverSeen = config.getConfigValueAsString("message.plot.list.interact.memberNeverSeen");
		Message.Plot.List.Interact.claimedBy = config.getConfigValueAsString("message.plot.list.interact.claimedBy");
		Message.Plot.List.Interact.plotName = config.getConfigValueAsString("message.plot.list.interact.plotName");
		Message.Plot.List.Interact.playerDoesNotOwnPlots = config.getConfigValueAsString("message.plot.list.interact.playerDoesNotOwnPlots");
		Message.Plot.List.Interact.approved = config.getConfigValueAsString("message.plot.list.interact.approved");
		Message.Plot.List.Interact.votes = config.getConfigValueAsString("message.plot.list.interact.votes");

		Message.Plot.List.Old.listFormat = config.getConfigValueAsString("message.plot.list.old.listFormat");
		Message.Plot.List.Old.notFound = config.getConfigValueAsString("message.plot.list.old.notFound");
		Message.Plot.List.Old.foundNumber = config.getConfigValueAsString("message.plot.list.old.foundNumber");
		Message.Plot.List.Old.listShowing = config.getConfigValueAsString("message.plot.list.old.listShowing");
		Message.Plot.List.Old.reasonNull = config.getConfigValueAsString("message.plot.list.old.reasonNull");
		Message.Plot.List.Old.reasonBanned = config.getConfigValueAsString("message.plot.list.old.reasonBanned");

		Message.Plot.Paintbrush.summon = config.getConfigValueAsString("message.plot.paintbrush.summon");
		Message.Plot.Paintbrush.noPermission = config.getConfigValueAsString("message.plot.paintbrush.noPermission");
		Message.Plot.Paintbrush.blockChanged = config.getConfigValueAsString("message.plot.paintbrush.blockChanged");

		Message.Plot.Regenerate.failApproved = config.getConfigValueAsString("message.plot.regenerate.failApproved");
		Message.Plot.Regenerate.rightClick = config.getConfigValueAsString("message.plot.regenerate.rightClick");
		Message.Plot.Regenerate.fail = config.getConfigValueAsString("message.plot.regenerate.fail");
		Message.Plot.Regenerate.success = config.getConfigValueAsString("message.plot.regenerate.success");

		Message.Plot.Rename.fail = config.getConfigValueAsString("message.plot.rename.fail");
		Message.Plot.Rename.success = config.getConfigValueAsString("message.plot.rename.success");

		Message.Plot.Scan.itemsFound = config.getConfigValueAsString("message.plot.scan.itemsFound");
		Message.Plot.Scan.listFormat = config.getConfigValueAsString("message.plot.scan.listFormat");

		Message.Plot.SetEntrance.failNoPermission = config.getConfigValueAsString("message.plot.setEntrance.failNoPermission");
		Message.Plot.SetEntrance.success = config.getConfigValueAsString("message.plot.setEntrance.success");

		Message.Plot.Unapprove.failNoPermission = config.getConfigValueAsString("message.plot.unapprove.failNoPermission");
		Message.Plot.Unapprove.failNotApproved = config.getConfigValueAsString("message.plot.unapprove.failNotApproved");
		Message.Plot.Unapprove.success = config.getConfigValueAsString("message.plot.unapprove.success");

		Message.Plot.Vote.failNoPermission = config.getConfigValueAsString("message.plot.vote.failNoPermission");
		Message.Plot.Vote.failError = config.getConfigValueAsString("message.plot.vote.failError");
		Message.Plot.Vote.success = config.getConfigValueAsString("message.plot.vote.success");

		Message.Plot.Tag.updateFail = config.getConfigValueAsString("message.plot.tag.updateFail");
		Message.Plot.Tag.updateSuccess = config.getConfigValueAsString("message.plot.tag.updateSuccess");
		Message.Plot.Tag.clearFail = config.getConfigValueAsString("message.plot.tag.clearFail");
		Message.Plot.Tag.clearSuccess = config.getConfigValueAsString("message.plot.tag.clearSuccess");
		Message.Plot.Tag.findFailNoTags = config.getConfigValueAsString("message.plot.tag.findFailNoTags");
		Message.Plot.Tag.findSuccessTooMany = config.getConfigValueAsString("message.plot.tag.findSuccessTooMany");
		Message.Plot.Tag.findSuccess = config.getConfigValueAsString("message.plot.tag.findSuccess");
		Message.Plot.Tag.setFail = config.getConfigValueAsString("message.plot.tag.setFail");
		Message.Plot.Tag.setSuccess = config.getConfigValueAsString("message.plot.tag.setSuccess");

		Message.Plot.Member.addFailBlacklisted = config.getConfigValueAsString("message.plot.member.addFailBlacklisted");
		Message.Plot.Member.addFail = config.getConfigValueAsString("message.plot.member.addFail");
		Message.Plot.Member.addSuccess = config.getConfigValueAsString("message.plot.member.addSuccess");
		Message.Plot.Member.removeFail = config.getConfigValueAsString("message.plot.member.removeFail");
		Message.Plot.Member.removeSuccess = config.getConfigValueAsString("message.plot.member.removeSuccess");
		Message.Plot.Member.Blacklist.failAlreadyBlacklisted = config.getConfigValueAsString("message.plot.member.blacklist.failAlreadyBlacklisted");
		Message.Plot.Member.Blacklist.success = config.getConfigValueAsString("message.plot.member.blacklist.success");
		Message.Plot.Member.Blacklist.whitelistFail = config.getConfigValueAsString("message.plot.member.blacklist.whitelistFail");
		Message.Plot.Member.Blacklist.whitelistSuccess = config.getConfigValueAsString("message.plot.member.blacklist.whitelistSuccess");
	}

	public static final class Message
	{
		public static String wrongWorld;
		public static String noWorldGuard;
		public static String noWorldDefined;
		public static String invalidLocation;

		public static final class Ai
		{
			public static String pattern;
			public static String needPermissionNoStaff;
			public static String needPermissionStaff;
			public static String plotsNotApproved;
			public static String findFreePlot;
			public static String claimCurrentPlot;
		}
		public static final class Plot
		{
			public static String invalid;
			public static String invalid2;
			public static String notFound;
			public static String noPlotsOwned;
			public static String notOwner;
			public static final class Approve
			{
				public static String invalid;
				public static String fail;
				public static String approvedFalse;
				public static String approvedTrue;
				public static String global;
			}

			public static final class Claim
			{
				public static String failPreexisting;
				public static String failNotClaimable;
				public static String failOtherNoPermissions;
				public static String failPreviousNotApproved;
				public static String fail;
				public static String succeedSelf;
				public static String succeedOther;
			}

			public static final class Clean
			{
				public static String itemsCleaned;
				public static String listFormat;
				public static String cleanFail;
				public static String cleanFailNoWorld;
				public static String blocksCleaned;
				public static String cleanRoadsFail;
				public static String cleanRoadsSuccess;
			}

			public static final class Delete
			{
				public static String failApproved;
				public static String rightClick;
				public static String success;
			}

			public static final class Extend
			{
				public static String rightClick;
				public static String failOverlap;
				public static String failError;
				public static String success;
			}

			public static final class Teleport
			{
				public static String findFailNoneLeft;
				public static String randomFailNoPlotsTagged;
				public static String failedNoList;
				public static String list;
				public static String success;
				public static String jump;
			}

			public static final class List
			{
				public static String command;

				public static final class Interact
				{
					public static String genericOwner;
					public static String genericMember;
					public static String tags;
					public static String member;
					public static String memberTime;
					public static String memberNeverSeen;
					public static String claimedBy;
					public static String plotName;
					public static String playerDoesNotOwnPlots;
					public static String approved;
					public static String votes;
				}

				public static final class Old
				{
					public static String listFormat;
					public static String notFound;
					public static String foundNumber;
					public static String listShowing;
					public static String reasonNull;
					public static String reasonBanned;
				}
			}

			public static final class Paintbrush
			{
				public static String summon;
				public static String noPermission;
				public static String blockChanged;
			}

			public static final class Regenerate
			{
				public static String failApproved;
				public static String rightClick;
				public static String fail;
				public static String success;
			}

			public static final class Rename
			{
				public static String fail;
				public static String success;
			}

			public static final class Scan
			{
				public static String itemsFound;
				public static String listFormat;
			}

			public static final class SetEntrance
			{
				public static String failNoPermission;
				public static String success;
			}

			public static final class Unapprove
			{
				public static String failNoPermission;
				public static String failNotApproved;
				public static String success;
			}

			public static final class Vote
			{
				public static String failNoPermission;
				public static String failError;
				public static String success;
			}

			public static final class Tag
			{
				public static String updateFail;
				public static String updateSuccess;
				public static String clearFail;
				public static String clearSuccess;
				public static String findFailNoTags;
				public static String findSuccessTooMany;
				public static String findSuccess;
				public static String setFail;
				public static String setSuccess;
			}

			public static final class Member
			{
				public static String addFailBlacklisted;
				public static String addFail;
				public static String addSuccess;
				public static String removeFail;
				public static String removeSuccess;

				public static final class Blacklist
				{
					public static String failAlreadyBlacklisted;
					public static String success;
					public static String whitelistFail;
					public static String whitelistSuccess;
				}
			}
		}
	}

	public static int getOldPlotsListLimit()
	{
		return max_listed;
	}

	public static List<String> getCleanFilter()
	{
		return noClean;
	}

	public static String getPaintbrushName()
	{
		return paintbrushName;
	}

	private static int max_listed;
	private static List<String> noClean;
	private static String paintbrushName;
}
