package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MergePullRequestOption
{
	@JsonProperty("Do")
	public String doStrategy;
	@JsonProperty("MergeTitleField")
	public String mergeTitle;
	@JsonProperty("MergeMessageField")
	public String mergeMessage;
	@JsonProperty("MergeCommitID")
	public String mergeCommitId;
	@JsonProperty("delete_branch_after_merge")
	public Boolean deleteBranchAfterMerge;
	@JsonProperty("force_merge")
	public Boolean forceMerge;
	@JsonProperty("head_commit_id")
	public String headCommitId;
	@JsonProperty("merge_when_checks_succeed")
	public Boolean mergeWhenChecksSucceed;
}
