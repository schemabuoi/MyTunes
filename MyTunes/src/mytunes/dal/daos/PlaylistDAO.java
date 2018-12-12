/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.dal.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Playlist;
import mytunes.dal.DbConnectionProvider;

/**
 *
 * @author Acer
 */
public class PlaylistDAO {
    
    private DbConnectionProvider connector;
    private PlaylistSongsDAO psDao;
    
    public PlaylistDAO()
    {
        connector = new DbConnectionProvider();
        psDao = new PlaylistSongsDAO();
    }
    
    public Playlist createPlaylist(String name) throws SQLException
    {
        String sqlStatement = "INSERT INTO Playlists(name) values(?)";
        try(Connection con = connector.getConnection();
                PreparedStatement statement = con.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, name);
            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            return new Playlist(id, name);
        }
    }
    
    public Playlist updatePlaylist(Playlist playlist, String newName) throws SQLException
    {
        String sqlStatement = "UPDATE Playlists SET name=? WHERE id=?";
        try(Connection con = connector.getConnection();
                PreparedStatement statement = con.prepareStatement(sqlStatement))
        {
            statement.setString(1, newName);
            statement.setInt(2, playlist.getId());
            statement.execute();
            playlist.setName(newName);
            return playlist;
        }
    }
    
    public List<Playlist> getAllPlaylists() throws SQLException
    {
        String sqlStatement = "SELECT * FROM Playlists";
        List<Playlist> allPlaylists = new ArrayList();
        try(Connection con = connector.getConnection();
                PreparedStatement statement = con.prepareStatement(sqlStatement))
        {
            ResultSet rs = statement.executeQuery();
            while(rs.next())
            {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                allPlaylists.add(new Playlist(id, name));
            }
            psDao.addAllSongsToAllPlaylists(allPlaylists);
        }
        return allPlaylists;
    }
    
    public void deletePlaylist(Playlist playlist) throws SQLException
    {
        psDao.deleteAllSongsFromPlaylist(playlist);
        String sqlStatement = "DELETE FROM Playlists WHERE id=?";
        try(Connection con = connector.getConnection();
                PreparedStatement statement = con.prepareStatement(sqlStatement))
        {
            statement.setInt(1, playlist.getId());
            statement.execute();
        }
    }
    
}
