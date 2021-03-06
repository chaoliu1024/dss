USE [dss]
GO
/****** Object:  Table [dbo].[role]    Script Date: 08/16/2015 18:12:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[role](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[role_name] [varchar](50) NOT NULL,
	[role_value] [varchar](50) NOT NULL,
	[login_state] [bit] NOT NULL,
 CONSTRAINT [PK_role] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[role] ON
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (7, N'艺术裁判01', N'artJudge01', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (8, N'艺术裁判02', N'artJudge02', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (9, N'艺术裁判03', N'artJudge03', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (23, N'艺术裁判04', N'artJudge04', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (24, N'完成裁判01', N'execJudge01', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (25, N'完成裁判02', N'execJudge02', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (26, N'完成裁判03', N'execJudge03', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (27, N'完成裁判04', N'execJudge04', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (28, N'舞步裁判01', N'impJudge01', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (31, N'舞步裁判02', N'impJudge02', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (33, N'高级裁判组01', N'seniorJudge01', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (36, N'高级裁判组02', N'seniorJudge02', 0)
INSERT [dbo].[role] ([id], [role_name], [role_value], [login_state]) VALUES (37, N'高级裁判组03', N'seniorJudge03', 0)
SET IDENTITY_INSERT [dbo].[role] OFF
/****** Object:  Table [dbo].[match_order]    Script Date: 08/16/2015 18:12:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[match_order](
	[id] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[match_num] [int] NOT NULL,
	[match_order] [int] NOT NULL,
	[match_units] [nvarchar](100) NOT NULL,
	[final_preliminary] [bit] NOT NULL,
	[match_name] [nvarchar](100) NOT NULL,
	[unit_status] [int] NOT NULL,
	[match_category] [nvarchar](100) NULL,
	[member_name] [nvarchar](max) NULL,
 CONSTRAINT [PK_fore_skill_team_infor] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[getTopTwoScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		liuchao
-- Create date: 2013.5.16
-- Description:	返回最高的两个分数
-- =============================================
CREATE FUNCTION [dbo].[getTopTwoScore]
(
	@score1 decimal(6, 1),
	@score2 decimal(6, 1),
	@score3 decimal(6, 1),
	@score4 decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型

AS
BEGIN
	-- Declare the return variable here
	DECLARE @maxScore decimal(6, 1), @maxSecScore decimal(6, 1), @Result decimal (6, 1);
	
	--若为NULL,转为0
	set @score1 = ISNULL(@score1,0)
	set @score2 = ISNULL(@score2,0)
	set @score3 = ISNULL(@score3,0)
	set @score4 = ISNULL(@score4,0)
	
	--得到最高分
	IF(@score1 > @score2)
	Begin
		set @maxScore = @score1
	End
	ELSE
	Begin
		set @maxScore = @score2
	End
	IF(@maxScore < @score3)
	Begin
		set @maxScore = @score3
	End
	IF(@maxScore < @score4)
	Begin
		set @maxScore = @score4
	End
	
	--@score1最高分 
	IF(@score1 = @maxScore)
	Begin
		IF(@score2 > @score3)
		Begin
			set @maxSecScore = @score2
		End
		ELSE
		Begin
			set @maxSecScore = @score3
		End
		IF(@maxSecScore < @score4)
		Begin
			set @maxSecScore = @score4
		End
		set @Result = @maxScore + @maxSecScore
	End
	
	--@score2最高分
	IF(@score2 = @maxScore)
	Begin
		IF(@score1 > @score3)
		Begin
			set @maxSecScore = @score1
		End
		ELSE
		Begin
			set @maxSecScore = @score3
		End
		IF(@maxSecScore < @score4)
		Begin
			set @maxSecScore = @score4
		End
		set @Result = @maxScore + @maxSecScore
	End
	
	--@score3最高分
	IF(@score3 = @maxScore)
	Begin
		IF(@score1 > @score2)
		Begin
			set @maxSecScore = @score1
		End
		ELSE
		Begin
			set @maxSecScore = @score2
		End
		IF(@maxSecScore < @score4)
		Begin
			set @maxSecScore = @score4
		End
		set @Result = @maxScore + @maxSecScore
	End
	
	--@score4最高分
	IF(@score4 = @maxScore)
	Begin
		IF(@score1 > @score2)
		Begin
			set @maxSecScore = @score1
		End
		ELSE
		Begin
			set @maxSecScore = @score2
		End
		IF(@maxSecScore < @score3)
		Begin
			set @maxSecScore = @score3
		End
		set @Result = @maxScore + @maxSecScore
	End
	
	RETURN @Result

END
GO
/****** Object:  UserDefinedFunction [dbo].[getTopThreeScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		liuchao
-- Create date: 2013.5.16
-- Description:	返回最高的三个分数
-- =============================================
CREATE FUNCTION [dbo].[getTopThreeScore]
(
	@score1 decimal(6, 1),
	@score2 decimal(6, 1),
	@score3 decimal(6, 1),
	@score4 decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型

AS
BEGIN
	-- Declare the return variable here
	DECLARE @maxScore decimal(6, 1), @Result decimal (6, 1);
	
	--若为NULL,转为0
	set @score1 = ISNULL(@score1,0)
	set @score2 = ISNULL(@score2,0)
	set @score3 = ISNULL(@score3,0)
	set @score4 = ISNULL(@score4,0)
	
	--得到最高分
	IF(@score1 > @score2)
	Begin
		set @maxScore = @score1
	End
	ELSE
	Begin
		set @maxScore = @score2
	End
	IF(@maxScore < @score3)
	Begin
		set @maxScore = @score3
	End
	IF(@maxScore < @score4)
	Begin
		set @maxScore = @score4
	End
	
	IF(@score1 = @maxScore)
	Begin
		set @Result = @score3 + @score3 + @score4
	End
	
	IF(@score2 = @maxScore)
	Begin
		set @Result = @score1 + @score3 + @score4
	End
	
	IF(@score3 = @maxScore)
	Begin
		set @Result = @score1 + @score2 + @score4
	End
	
	IF(@score4 = @maxScore)
	Begin
		set @Result = @score1 + @score2 + @score3
	End
	-- Return the result of the function
	RETURN @Result

END
GO
/****** Object:  UserDefinedFunction [dbo].[getTopScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		liuchao
-- Create date: 2013.5.16
-- Description:	返回最高分数
-- =============================================
CREATE FUNCTION [dbo].[getTopScore]
(
	@score1 decimal(6, 1),
	@score2 decimal(6, 1),
	@score3 decimal(6, 1),
	@score4 decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型

AS
BEGIN
	-- Declare the return variable here
	DECLARE @maxScore decimal(6, 1);
	
	--若为NULL,转为0
	set @score1 = ISNULL(@score1,0)
	set @score2 = ISNULL(@score2,0)
	set @score3 = ISNULL(@score3,0)
	set @score4 = ISNULL(@score4,0)
	
	--得到最高分
	IF(@score1 > @score2)
	Begin
		set @maxScore = @score1
	End
	ELSE
	Begin
		set @maxScore = @score2
	End
	IF(@maxScore < @score3)
	Begin
		set @maxScore = @score3
	End
	IF(@maxScore < @score4)
	Begin
		set @maxScore = @score4
	End
	
	RETURN @maxScore

END
GO
/****** Object:  UserDefinedFunction [dbo].[getSkillTotalScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date, ,>
-- Description:	<Description, ,>
-- =============================================
create FUNCTION [dbo].[getSkillTotalScore]
(
	-- Add the parameters for the function here
	@score1 decimal(6, 1),
	@score2 decimal(6, 1),
    @score3 decimal(6, 1),
	@score4 decimal(6, 1),
    @score5 decimal(6, 1),
	@score6 decimal(6, 1),
    @score7 decimal(6, 1),
	@score8 decimal(6, 1),
    @score9 decimal(6, 1),
    @score10 decimal(6, 1),
	@sub_score decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型
AS
BEGIN
	-- Declare the return variable here
	
	DECLARE @Result decimal (6, 1)
	set @Result=0;

	----若分1不为空
	IF(@score1 is not null)
	Begin
		set @Result = @Result+@score1;
	End
	----若分2不为空
	IF(@score2 is not null)
	Begin
		set @Result = @Result+@score2;
	End
   ----若分3不为空
	IF(@score3 is not null)
	Begin
		set @Result = @Result+@score3;
	End
    ----若分4不为空
	IF(@score4 is not null)
	Begin
		set @Result = @Result+@score4;
	End
     ----若分5不为空
	IF(@score5 is not null)
	Begin
		set @Result = @Result+@score5;
	End
    ----若分6不为空
	IF(@score6 is not null)
	Begin
		set @Result = @Result+@score6;
	End
    ----若分7不为空
	IF(@score7 is not null)
	Begin
		set @Result = @Result+@score7;
	End
	----若分8不为空
	IF(@score8 is not null)
	Begin
		set @Result = @Result+@score8;
	End
	----若分9不为空
	IF(@score9 is not null)
	Begin
		set @Result = @Result+@score9;
	End
    ----若分10不为空
	IF(@score10 is not null)
	Begin
		set @Result = @Result+@score10;
	End
    ----若减分不为空
	IF(@sub_score is not null)
	Begin
		set @Result = @Result-@sub_score;
	End
	RETURN @Result
END
GO
/****** Object:  UserDefinedFunction [dbo].[getMidScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		liuchao
-- Create date: 2013.5.16
-- Description:	得到非最高和最低的分数
-- =============================================
CREATE FUNCTION [dbo].[getMidScore]
(
	@score1 decimal(6, 1),
	@score2 decimal(6, 1),
	@score3 decimal(6, 1),
	@score4 decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型

AS
BEGIN
	-- Declare the return variable here
	DECLARE @maxScore decimal(6, 1), @minScore decimal(6, 1), @temp1 decimal(6, 1), @temp2 decimal(6, 1),
	@Result decimal(6, 1);
	
	--若为NULL,转为0
	set @score1 = ISNULL(@score1,0)
	set @score2 = ISNULL(@score2,0)
	set @score3 = ISNULL(@score3,0)
	set @score4 = ISNULL(@score4,0)
	
	--赋初值
	set @temp1 = 0.0
	set @temp2 = 0.0
	
	--得到最高分
	IF(@score1 > @score2)
	Begin
		set @maxScore = @score1
	End
	ELSE
	Begin
		set @maxScore = @score2
	End
	IF(@maxScore < @score3)
	Begin
		set @maxScore = @score3
	End
	IF(@maxScore < @score4)
	Begin
		set @maxScore = @score4
	End
	
	--得到最低分
	IF(@score1 < @score2)
	Begin
		set @minScore = @score1
	End
	ELSE
	Begin
		set @minScore = @score2
	End
	IF(@minScore > @score3)
	Begin
		set @minScore = @score3
	End
	IF(@minScore > @score4)
	Begin
		set @minScore = @score4
	End
	
	--得到中间两个分平均分
	IF((@score1 = @maxScore) and (@score2 = @minScore))
	Begin
		set @temp1 = @score3
		set @temp2 = @score4
	End
	IF((@score1 = @maxScore) and (@score3 = @minScore))
	Begin
		set @temp1 = @score2
		set @temp2 = @score4
	End
	IF((@score1 = @maxScore) and (@score4 = @minScore))
	Begin
		set @temp1 = @score2
		set @temp2 = @score3
	End
	
	IF((@score2 = @maxScore) and (@score1 = @minScore))
	Begin
		set @temp1 = @score3
		set @temp2 = @score4
	End
	IF((@score2 = @maxScore) and (@score3 = @minScore))
	Begin
		set @temp1 = @score1
		set @temp2 = @score4
	End
	IF((@score2 = @maxScore) and (@score4 = @minScore))
	Begin
		set @temp1 = @score1
		set @temp2 = @score3
	End
	
	IF((@score3 = @maxScore) and (@score1 = @minScore))
	Begin
		set @temp1 = @score2
		set @temp2 = @score4
	End
	IF((@score3 = @maxScore) and (@score2 = @minScore))
	Begin
		set @temp1 = @score1
		set @temp2 = @score4
	End
	IF((@score3 = @maxScore) and (@score4 = @minScore))
	Begin
		set @temp1 = @score1
		set @temp2 = @score2
	End
	
	IF((@score4 = @maxScore) and (@score1 = @minScore))
	Begin
		set @temp1 = @score2
		set @temp2 = @score3
	End
	IF((@score4 = @maxScore) and (@score2 = @minScore))
	Begin
		set @temp1 = @score1
		set @temp2 = @score3
	End
	IF((@score4 = @maxScore) and (@score3 = @minScore))
	Begin
		set @temp1 = @score1
		set @temp2 = @score2
	End
	
	set @Result = AVG(@temp1+@temp2)
	
	RETURN @Result

END
GO
/****** Object:  UserDefinedFunction [dbo].[getFourScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		liuchao
-- Create date: 得到四个分数总和
-- Description:	2013.5.16
-- =============================================
CREATE FUNCTION [dbo].[getFourScore]
(
	@score1 decimal(6, 1),
	@score2 decimal(6, 1),
	@score3 decimal(6, 1),
	@score4 decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型

AS
BEGIN
	-- Declare the return variable here
	DECLARE @Result decimal (6, 1);
	
	--若为NULL,转为0
	set @score1 = ISNULL(@score1,0)
	set @score2 = ISNULL(@score2,0)
	set @score3 = ISNULL(@score3,0)
	set @score4 = ISNULL(@score4,0)

	set @Result = @score1 + @score3 + @score3 + @score4

	-- Return the result of the function
	RETURN @Result

END
GO
/****** Object:  UserDefinedFunction [dbo].[getDanceTotalScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date, ,>
-- Description:	<Description, ,>
-- =============================================
CREATE FUNCTION [dbo].[getDanceTotalScore]
(
	-- Add the parameters for the function here
	@score1 decimal(6, 1),
	@score2 decimal(6, 1),
    @score3 decimal(6, 1),
	@score4 decimal(6, 1),
    @score5 decimal(6, 1),
	@score6 decimal(6, 1),
    @score7 decimal(6, 1),
	@sub_score decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型
AS
BEGIN
	-- Declare the return variable here
	
	DECLARE @Result decimal (6, 1)
	set @Result=0;

	----若分1不为空
	IF(@score1 is not null)
	Begin
		set @Result = @Result+@score1;
	End
	----若分2不为空
	IF(@score2 is not null)
	Begin
		set @Result = @Result+@score2;
	End
   ----若分3不为空
	IF(@score3 is not null)
	Begin
		set @Result = @Result+@score3;
	End
    ----若分4不为空
	IF(@score4 is not null)
	Begin
		set @Result = @Result+@score4;
	End
     ----若分5不为空
	IF(@score5 is not null)
	Begin
		set @Result = @Result+@score5;
	End
    ----若分6不为空
	IF(@score6 is not null)
	Begin
		set @Result = @Result+@score6;
	End
    ----若分7不为空
	IF(@score7 is not null)
	Begin
		set @Result = @Result+@score7;
	End
    ----若减分不为空
	IF(@sub_score is not null)
	Begin
		set @Result = @Result-@sub_score;
	End
	RETURN @Result
END
GO
/****** Object:  UserDefinedFunction [dbo].[getAvgDiffScore]    Script Date: 08/16/2015 18:12:21 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		liuchao
-- Create date: 2013.5.16
-- Description:	得到两个难度分的平均值
-- =============================================
CREATE FUNCTION [dbo].[getAvgDiffScore]
(
	-- Add the parameters for the function here
	@diffScore1 decimal(6, 1),
	@diffScore2 decimal(6, 1)
)
RETURNS decimal(6, 1)  ---- 返回值类型
AS
BEGIN
	-- Declare the return variable here
	
	DECLARE @Result decimal (6, 1)
	
	----若难度分1,2都为空
	IF(@diffScore1 is null and @diffScore2 is null)
	Begin
		set @Result = 0
	End
	
	----若难度分1,2都不为空
	IF(@diffScore1 is not null and @diffScore2 is not null)
	Begin
		set @Result = AVG(@diffScore1+@diffScore2)
	End
	
	----若难度分1为空,难度分2不为空
	IF(@diffScore1 is null and @diffScore2 is not null)
	Begin
		set @Result = @diffScore2
	End
	
	----若难度分1不为空,难度分2为空
	IF(@diffScore1 is not null and @diffScore2 is null)
	Begin
		set @Result = @diffScore1
	End
	
	RETURN @Result
END
GO
/****** Object:  Table [dbo].[config]    Script Date: 08/16/2015 18:12:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[config](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](30) NULL,
	[role] [nvarchar](30) NOT NULL,
	[location] [nvarchar](30) NULL,
 CONSTRAINT [PK_config] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[config] ON
INSERT [dbo].[config] ([id], [name], [role], [location]) VALUES (1, N'', N'intercessor', N'南京')
INSERT [dbo].[config] ([id], [name], [role], [location]) VALUES (2, N'', N'viceReferee', N'南京')
INSERT [dbo].[config] ([id], [name], [role], [location]) VALUES (3, N'', N'refree', N'南京')
SET IDENTITY_INSERT [dbo].[config] OFF
/****** Object:  Table [dbo].[score]    Script Date: 08/16/2015 18:12:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[score](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[team_id] [int] NOT NULL,
	[score01_art] [decimal](6, 2) NOT NULL,
	[score02_art] [decimal](6, 2) NOT NULL,
	[score03_art] [decimal](6, 2) NOT NULL,
	[score04_art] [decimal](6, 2) NOT NULL,
	[avg_art] [decimal](6, 2) NOT NULL,
	[score01_execution] [decimal](6, 2) NOT NULL,
	[score02_execution] [decimal](6, 2) NOT NULL,
	[score03_execution] [decimal](6, 2) NOT NULL,
	[score04_execution] [decimal](6, 2) NOT NULL,
	[avg_execution] [decimal](6, 2) NOT NULL,
	[score01_impression] [decimal](6, 2) NOT NULL,
	[score02_impression] [decimal](6, 2) NOT NULL,
	[avg_impression] [decimal](6, 2) NOT NULL,
	[sub_score] [decimal](6, 2) NULL,
	[total] [decimal](6, 2) NOT NULL,
	[score01_art_error] [decimal](4, 2) NULL,
	[score02_art_error] [decimal](4, 2) NULL,
	[score03_art_error] [decimal](4, 2) NULL,
	[score04_art_error] [decimal](4, 2) NULL,
	[score01_execution_error] [decimal](4, 2) NULL,
	[score02_execution_error] [decimal](4, 2) NULL,
	[score03_execution_error] [decimal](4, 2) NULL,
	[score04_execution_error] [decimal](4, 2) NULL,
	[score01_impression_error] [decimal](4, 2) NULL,
	[score02_impression_error] [decimal](4, 2) NULL,
 CONSTRAINT [PK_score] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  ForeignKey [FK_score_match_order]    Script Date: 08/16/2015 18:12:18 ******/
ALTER TABLE [dbo].[score]  WITH CHECK ADD  CONSTRAINT [FK_score_match_order] FOREIGN KEY([team_id])
REFERENCES [dbo].[match_order] ([id])
GO
ALTER TABLE [dbo].[score] CHECK CONSTRAINT [FK_score_match_order]
GO
