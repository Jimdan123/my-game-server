package io.myserver;

import io.myserver.database.DBConnector;
import io.myserver.database.DummyDBConnector;
import io.myserver.user.UserManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.myserver.netty.NettyPipelineInitializer;

public class MyGameServer
{
	private static final int				_PORT = 8081;

	private static final MyGameServer 		_INSTANCE = new MyGameServer();

	public static MyGameServer getInstance()
	{
		return _INSTANCE;
	}

	private EventLoopGroup 					_boss_group;
	private EventLoopGroup 					_worker_group;

	private DBConnector						_db_conn;

	private MyGameServer()
	{

	}

	public void start() throws Exception
	{
		System.out.println("My Game Server: starting up...");

		//init database connection
		_db_conn = new DummyDBConnector("simpleDB.bin");
		_db_conn.connect();

		_boss_group = new NioEventLoopGroup(1);
		_worker_group = new NioEventLoopGroup(2);

		ServerBootstrap sbt = new ServerBootstrap();
		sbt.group(_boss_group, _worker_group)
				.channel(NioServerSocketChannel.class)
				.childHandler(new NettyPipelineInitializer());

		ChannelFuture f = sbt.bind(_PORT).sync();

		System.out.println("My Game Server: started");
		System.out.println("My Game Server: listening port " + _PORT);
		System.out.println(AsciiArt.READY);

		//setup shutdown process
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				stop();
			}
		}, "ShutdownThread"));

		// wait here
		f.channel().closeFuture().sync();
	}

	public synchronized void stop()
	{
		System.out.println("My Game Server: shutting down...");

		if (_boss_group != null)
		{
			try
			{
				_boss_group.shutdownGracefully();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				_boss_group = null;
			}
		}

		if (_worker_group != null)
		{
			try
			{
				_worker_group.shutdownGracefully();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				_worker_group = null;
			}
		}

		UserManager.getInstance().saveAllUsers();

		if (_db_conn != null)
		{
			try
			{
				_db_conn.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				_db_conn = null;
			}
		}
	}

	public DBConnector getDbConn()
	{
		return _db_conn;
	}
}
