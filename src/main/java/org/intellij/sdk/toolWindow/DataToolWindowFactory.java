// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.intellij.sdk.toolWindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

final class DataToolWindowFactory implements ToolWindowFactory, DumbAware {

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    DataToolWindowContent toolWindowContent = new DataToolWindowContent(project, toolWindow);
    Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
    toolWindow.getContentManager().addContent(content);
  }

  private static class DataToolWindowContent {

    private final Project project;
    private final JPanel contentPanel = new JPanel(new BorderLayout());
    private final JBTable dataTable;
    private final DefaultTableModel tableModel;

    public DataToolWindowContent(Project project, ToolWindow toolWindow) {
      this.project = project;

      // Setup Table
      String[] columnNames = {"项目名称", "项目类型", "项目产线", "服务名称", "重要性", "Owner", "操作"};
      tableModel = new DefaultTableModel(columnNames, 0);
      dataTable = new JBTable(tableModel);

      // Create ToolbarDecorator
      ToolbarDecorator decorator = ToolbarDecorator.createDecorator(dataTable);
      decorator.addExtraAction(new AnAction("刷新", "刷新表格数据", AllIcons.Actions.Refresh) {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
          fetchData();
        }
      });
      decorator.addExtraAction(new AnAction("查找 app.id", "在项目中查找 app.properties 文件", AllIcons.Actions.Find) {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
          findAndShowAppId();
        }
      });
      decorator.addExtraAction(new AnAction("隐藏", "隐藏工具窗口", AllIcons.General.HideToolWindow) {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
          toolWindow.hide(null);
        }
      });

      // Create the panel from the decorator
      JPanel decoratedPanel = decorator.createPanel();
      contentPanel.add(decoratedPanel, BorderLayout.CENTER);

      addTableMouseListener();
    }

    private void addTableMouseListener() {
        dataTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = dataTable.rowAtPoint(e.getPoint());
                int col = dataTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    // For now, we'll just show a message with the row data
                    StringBuilder rowData = new StringBuilder();
                    for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        rowData.append(tableModel.getColumnName(i))
                               .append(": ")
                               .append(tableModel.getValueAt(row, i))
                               .append("\n");
                    }
                    Messages.showMessageDialog(contentPanel, rowData.toString(), "行数据", Messages.getInformationIcon());
                }
            }
        });
    }

    private void findAndShowAppId() {
        ApplicationManager.getApplication().runReadAction(() -> {
            PsiFile[] files = FilenameIndex.getFilesByName(project, "app.properties", GlobalSearchScope.projectScope(project));
            if (files.length == 0) {
                Messages.showMessageDialog(contentPanel, "未在项目中找到 app.properties 文件。", "错误", Messages.getErrorIcon());
                return;
            }

            VirtualFile virtualFile = files[0].getVirtualFile();
            if (virtualFile == null) {
                Messages.showMessageDialog(contentPanel, "无法获取文件的 VirtualFile。", "错误", Messages.getErrorIcon());
                return;
            }

            try {
                String content = new String(virtualFile.contentsToByteArray());
                Properties properties = new Properties();
                properties.load(new StringReader(content));
                String appId = properties.getProperty("app.id");

                if (appId != null) {
                    Messages.showMessageDialog(contentPanel, "app.id 的值为: " + appId, "找到 App ID", Messages.getInformationIcon());
                } else {
                    Messages.showMessageDialog(contentPanel, "在 app.properties 文件中未找到 app.id。", "错误", Messages.getErrorIcon());
                }
            } catch (IOException e) {
                Messages.showMessageDialog(contentPanel, "读取 app.properties 文件时出错: " + e.getMessage(), "错误", Messages.getErrorIcon());
            }
        });
    }


    private void fetchData() {
      // Clear existing data
      tableModel.setRowCount(0);

      // Add mock data
      Object[] row1 = {"商旅结算账单报表服务(100037402)", "SOA(24442)", "商旅", "CorpSettlementBillService", "核心", "cxwang", "详情"};
      Object[] row2 = {"酒店订单服务(100037403)", "Microservice(24443)", "酒店", "HotelOrderService", "重要", "zhangsan", "详情"};
      Object[] row3 = {"机票查询服务(100037404)", "SOA(24444)", "机票", "FlightQueryService", "一般", "lisi", "详情"};

      tableModel.addRow(row1);
      tableModel.addRow(row2);
      tableModel.addRow(row3);
    }

    public JPanel getContentPanel() {
      return contentPanel;
    }
  }
}
