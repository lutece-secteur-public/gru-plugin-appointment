/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.paris.lutece.plugins.workflow.modules.notification.service.TaskNotification;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroupHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Service for import/export Workflow in json
 *
 * @author Laurent Payen
 *
 */
public final class WorkflowTraderService
{

    // PROPERTIES
    private static final String ACTION = "action";
    private static final String ACTIONS = "actions";
    private static final String CONFIGS = "configs";
    private static final String ID = "id";
    private static final String ID_TASK = "idTask";
    private static final String IMPORT = "Import";
    private static final String KEY = "key";
    private static final String STATES = "states";
    private static final String TASKS = "tasks";
    private static final String TASK_TYPE = "taskType";
    private static final String WORKFLOW = "workflow";

    private static IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private static IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private static IStateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private static ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );

    private static ObjectMapper _mapper = new ObjectMapper( ).registerModule( new JavaTimeModule( ) ).setSerializationInclusion( Include.NON_NULL );

    /**
     * Import a workflow from a json object
     *
     * @param jsonObject
     *            the json object
     * @return the id of the workflow created
     */
    public static int importWorkflowFromJson( JSONObject jsonObject )
    {
        int nIdWorkflow = 0;
        try
        {
            Workflow workflow = null;
            Object objectWorkflow = jsonObject.get( WORKFLOW );
            if ( objectWorkflow != null )
            {
                workflow = _mapper.readValue( objectWorkflow.toString( ), Workflow.class );
            }
            if ( workflow != null )
            {
                // To avoid duplicate name
                WorkflowFilter filter = new WorkflowFilter( );
                filter.setName( workflow.getName( ) );
                // Check if a workflow with this name already exists
                if ( CollectionUtils.isNotEmpty( _workflowService.getListWorkflowsByFilter( filter ) ) )
                {
                    // Add import in front of the name
                    workflow.setName( IMPORT + StringUtils.SPACE + workflow.getName( ) );
                }
                // Check if the workgroup of the imported workflow exists
                String strWorkgroup = workflow.getWorkgroup( );
                if ( StringUtils.isNotEmpty( strWorkgroup ) && AdminWorkgroupHome.checkExistWorkgroup( strWorkgroup ) )
                {
                    workflow.setWorkgroup( strWorkgroup );
                }
                else
                {
                    workflow.setWorkgroup( StringUtils.EMPTY );
                }
                _workflowService.create( workflow );
                nIdWorkflow = workflow.getId( );
                HashMap<Integer, State> mapIdState = importStates( jsonObject, workflow );
                HashMap<Integer, Action> mapIdAction = importActions( jsonObject, mapIdState, workflow );
                HashMap<Integer, Integer> mapIdTask = importTasks( jsonObject, mapIdAction );
                importConfigs( jsonObject, mapIdTask );
            }
        }
        catch( IOException | ClassNotFoundException e )
        {
            AppLogService.error( "Erreur lors de l'import du json", e );
        }
        return nIdWorkflow;
    }

    /**
     * Export a workflow to a json object
     *
     * @param nIdWorkflow
     *            the id of the workflow
     * @param locale
     *            the locale
     * @return the json object of the workflow
     */
    public static JSONObject exportWorkflowToJson( int nIdWorkflow, Locale locale )
    {
        JSONObject jsonObject = new JSONObject( );
        Workflow workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        try
        {
            if ( workflow != null )
            {
                jsonObject.put( WORKFLOW, _mapper.writeValueAsString( workflow ) );
                exportStates( jsonObject, nIdWorkflow );
                exportActionsTasksAndConfigs( jsonObject, nIdWorkflow, locale );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( "Error during import of the json", e );
        }
        return jsonObject;
    }

    /**
     * Import the states of a workflow
     *
     * @param jsonObject
     *            the json object
     * @param workflow
     *            the workflow
     * @return a map of the state with key id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static HashMap<Integer, State> importStates( JSONObject jsonObject, Workflow workflow ) throws JsonParseException, JsonMappingException, IOException
    {
        HashMap<Integer, State> mapIdState = new HashMap<>( );
        List<State> listStates = new ArrayList<>( );
        JSONArray jsArrayStates = null;
        if ( jsonObject.containsKey( STATES ) )
        {
            jsArrayStates = jsonObject.getJSONArray( STATES );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayStates ) )
        {
            listStates = Arrays.asList( _mapper.readValue( jsArrayStates.toString( ), State [ ].class ) );
        }
        int nOldIdState;
        for ( State state : listStates )
        {
            nOldIdState = state.getId( );
            state.setWorkflow( workflow );
            _stateService.create( state );
            mapIdState.put( nOldIdState, state );
        }
        return mapIdState;
    }

    /**
     * Import the actions of a workflow
     *
     * @param jsonObject
     *            the json object
     * @param mapIdState
     *            the map of the id of the states
     * @param workflow
     *            the workflow
     * @return a map of the action with key id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static HashMap<Integer, Action> importActions( JSONObject jsonObject, HashMap<Integer, State> mapIdState, Workflow workflow )
            throws JsonParseException, JsonMappingException, IOException
    {
        HashMap<Integer, Action> mapIdAction = new HashMap<>( );
        List<Action> listActions = new ArrayList<>( );
        JSONArray jsArrayActions = null;
        if ( jsonObject.containsKey( ACTIONS ) )
        {
            jsArrayActions = jsonObject.getJSONArray( ACTIONS );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayActions ) )
        {
            listActions = Arrays.asList( _mapper.readValue( jsArrayActions.toString( ), Action [ ].class ) );
        }
        int nOldIdAction;
        for ( Action action : listActions )
        {
            nOldIdAction = action.getId( );
            action.setStateBefore( mapIdState.get( action.getStateBefore( ).getId( ) ) );
            action.setStateAfter( mapIdState.get( action.getStateAfter( ).getId( ) ) );
            action.setWorkflow( workflow );
            _actionService.create( action );
            mapIdAction.put( nOldIdAction, action );
        }
        List<Integer> listNewIdsLinkedAction;
        for ( Action action : listActions )
        {
            if ( CollectionUtils.isNotEmpty( action.getListIdsLinkedAction( ) ) )
            {
                listNewIdsLinkedAction = new ArrayList<>( );
                for ( int oldIdLinkedAction : action.getListIdsLinkedAction( ) )
                {
                    listNewIdsLinkedAction.add( mapIdAction.get( oldIdLinkedAction ).getId( ) );
                }
                action.setListIdsLinkedAction( listNewIdsLinkedAction );
                _actionService.update( action );
            }
        }
        return mapIdAction;
    }

    /**
     * Import the tasks of a workflow
     *
     * @param jsonObject
     *            the json object
     * @param mapIdAction
     *            the map of the actions
     * @return a map of the task with key id
     */
    private static HashMap<Integer, Integer> importTasks( JSONObject jsonObject, HashMap<Integer, Action> mapIdAction )
    {
        HashMap<Integer, Integer> mapIdTask = new HashMap<>( );
        JSONArray jsArrayTasks = null;
        if ( jsonObject.containsKey( TASKS ) )
        {
            jsArrayTasks = jsonObject.getJSONArray( TASKS );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayTasks ) )
        {
            JSONObject jsObjTask;
            for ( Object objTask : jsArrayTasks )
            {
                jsObjTask = JSONObject.fromObject( objTask );
                ITask task = new TaskNotification( );
                JSONObject jsObjectAction = jsObjTask.getJSONObject( ACTION );
                Action action = new Action( );
                action.setId( mapIdAction.get( jsObjectAction.getInt( ID ) ).getId( ) );
                JSONObject jsonObjectTaskType = jsObjTask.getJSONObject( TASK_TYPE );
                ITaskType taskType = new TaskType( );
                taskType.setKey( jsonObjectTaskType.getString( KEY ) );
                task.setAction( action );
                int nMaximumOrder = _taskService.findMaximumOrderByActionId( task.getAction( ).getId( ) );
                task.setOrder( nMaximumOrder + 1 );
                task.setTaskType( taskType );
                _taskService.create( task );
                mapIdTask.put( jsObjTask.getInt( ID ), task.getId( ) );
            }
        }
        return mapIdTask;
    }

    /**
     * Import the configs of workflow
     *
     * @param jsonObject
     *            the json object
     * @param mapIdTask
     *            the map of the task
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void importConfigs( JSONObject jsonObject, HashMap<Integer, Integer> mapIdTask )
            throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException
    {
        JSONArray jsArrayConfigs = null;
        if ( jsonObject.containsKey( CONFIGS ) )
        {
            jsArrayConfigs = jsonObject.getJSONArray( CONFIGS );
        }
        List<ITaskConfigService> listTaskConfigService = SpringContextService.getBeansOfType( ITaskConfigService.class );
        if ( CollectionUtils.isNotEmpty( jsArrayConfigs ) )
        {
            for ( Object object : jsArrayConfigs )
            {
                JSONArray jsArrayObject = (JSONArray) object;
                String className = String.valueOf( jsArrayObject.get( 0 ) );
                Class<?> classImpl = Class.forName( className );
                ITaskConfig taskConfig = (ITaskConfig) _mapper.readValue( jsArrayObject.get( 1 ).toString( ), classImpl );
                JSONObject jsObject = JSONObject.fromObject( jsArrayObject.get( 1 ) );
                taskConfig.setIdTask( mapIdTask.get( jsObject.getInt( ID_TASK ) ) );
                for ( ITaskConfigService configService : listTaskConfigService )
                {
                    try
                    {
                        configService.create( taskConfig );
                        break;
                    }
                    catch( Exception e )
                    {
                        continue;
                    }
                }
            }
        }
    }

    /**
     * Export the states of a workflow into a json object
     *
     * @param jsonObject
     *            the json object
     * @param nIdWorkflow
     *            the workflow id
     * @throws JsonProcessingException
     */
    private static void exportStates( JSONObject jsonObject, int nIdWorkflow ) throws JsonProcessingException
    {
        JSONArray jsStates = new JSONArray( );
        List<State> listStates = new ArrayList<>( );
        listStates.addAll( _workflowService.getAllStateByWorkflow( nIdWorkflow ) );
        for ( State state : listStates )
        {
            jsStates.add( _mapper.writeValueAsString( state ) );
        }
        if ( CollectionUtils.isNotEmpty( jsStates ) )
        {
            jsonObject.put( STATES, jsStates );
        }
    }

    /**
     * Export tha actions, tasks and configs of a workflow to a json object
     *
     * @param jsonObject
     *            the json object
     * @param nIdWorkflow
     *            the workflow id
     * @param locale
     *            the locale
     * @throws JsonProcessingException
     */
    private static void exportActionsTasksAndConfigs( JSONObject jsonObject, int nIdWorkflow, Locale locale ) throws JsonProcessingException
    {
        Locale localeToUse = Locale.getDefault( );
        if ( locale != null )
        {
            localeToUse = locale;
        }
        JSONArray jsActions = new JSONArray( );
        JSONArray jsTasks = new JSONArray( );
        JSONArray jsConfig = new JSONArray( );
        ActionFilter actionFilter = new ActionFilter( );
        actionFilter.setIdWorkflow( nIdWorkflow );
        List<Action> listActions = _actionService.getListActionByFilter( actionFilter );
        ITaskConfig taskConfig = null;
        for ( Action action : listActions )
        {
            jsActions.add( _mapper.writeValueAsString( action ) );
            List<ITask> listTasks = _taskService.getListTaskByIdAction( action.getId( ), localeToUse );
            for ( ITask task : listTasks )
            {
                jsTasks.add( _mapper.writeValueAsString( task ) );
                List<ITaskConfigService> listTaskConfigService = SpringContextService.getBeansOfType( ITaskConfigService.class );
                for ( ITaskConfigService taskConfigService : listTaskConfigService )
                {
                    taskConfig = taskConfigService.findByPrimaryKey( task.getId( ) );
                    if ( taskConfig != null )
                    {
                        List<Object> listObjects = new ArrayList<>( );
                        listObjects.add( taskConfig.getClass( ) );
                        listObjects.add( taskConfig );
                        jsConfig.add( listObjects );
                    }
                }
            }
        }
        if ( CollectionUtils.isNotEmpty( jsActions ) )
        {
            jsonObject.put( ACTIONS, jsActions );
        }
        if ( CollectionUtils.isNotEmpty( jsTasks ) )
        {
            jsonObject.put( TASKS, jsTasks );
        }
        if ( CollectionUtils.isNotEmpty( jsConfig ) )
        {
            jsonObject.put( CONFIGS, jsConfig );
        }
    }
}
