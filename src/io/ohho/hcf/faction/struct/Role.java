package io.ohho.hcf.faction.struct;

public enum Role
{
  LEADER("Leader", "**"),  CAPTAIN("Captain", "*"),  MEMBER("Member", "");
  
  private final String name;
  private final String astrix;
  
  private Role(String name, String astrix)
  {
    this.name = name;
    this.astrix = astrix;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getAstrix()
  {
    return this.astrix;
  }
}
